package fr.uha.hassenforder.easy.memorial.ui.tour

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.app.UITitleBuilder
import fr.uha.hassenforder.easy.memorial.database.dao.GuideDao.GuideUpdate.Position
import fr.uha.hassenforder.easy.memorial.database.dao.TourDao.TourUpdate.Description
import fr.uha.hassenforder.easy.memorial.database.dao.TourDao.TourUpdate.Mode
import fr.uha.hassenforder.easy.memorial.database.dao.TourDao.TourUpdate.Picture
import fr.uha.hassenforder.easy.memorial.database.dao.TourDao.TourUpdate.Title
import fr.uha.hassenforder.easy.memorial.database.dao.TourDao.TourUpdate.WithDelay
import fr.uha.hassenforder.easy.memorial.database.dao.TourDao.TourUpdate.WithHint
import fr.uha.hassenforder.easy.memorial.database.dao.TourDao.TourUpdate.WithPicture
import fr.uha.hassenforder.easy.memorial.dto.FullGuide
import fr.uha.hassenforder.easy.memorial.model.Guide
import fr.uha.hassenforder.easy.memorial.dto.ManagedTourWithGuides
import fr.uha.hassenforder.easy.memorial.model.POI
import fr.uha.hassenforder.easy.memorial.model.Tour
import fr.uha.hassenforder.easy.memorial.model.TourMode
import fr.uha.hassenforder.easy.memorial.model.Trigger
import fr.uha.hassenforder.easy.memorial.model.WithHelper
import fr.uha.hassenforder.easy.memorial.navigation.TourDestination
import fr.uha.hassenforder.easy.memorial.repository.TourRepository
import fr.uha.hassenforder.easy.memorial.validation.TourValidationError
import fr.uha.hassenforder.easy.memorial.validation.TourValidator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map

@HiltViewModel
class TourViewModel @Inject constructor (
    private val repository: TourRepository,
    private val navigator: Navigator,
): ViewModel() {

    val uiTitleBuilder = UITitleBuilder()

    private val _uiTitle : StateFlow<UITitle> = uiTitleBuilder.uiTitle.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UITitle()
    )

    private val _id: MutableStateFlow<Long> = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _tour : StateFlow<Result<ManagedTourWithGuides>> = _id
        .flatMapLatest { id -> repository.getTourWithGuides(id) }
        .map { tour ->
            if (tour != null) {
                Result.Success(content = tour)
            } else {
                Result.Error()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    @Immutable
    data class UIState(
        val uiTitle : UITitle,
        val tour : ManagedTourWithGuides,
        val validation : TourValidationError,
    ) {
        companion object {
            fun create (uiTitle: UITitle, tour: ManagedTourWithGuides) : UIState {
                val validation = TourValidator(tour).validateAll()
                return UIState(uiTitle, tour, validation)
            }
        }
    }

    val uiState : StateFlow<Result<UIState>> = combine(_uiTitle, _tour)
        { t, tour ->
            when (tour) {
                is Result.Success -> Result.Success(content = UIState.create(t, tour.content))
                is Result.Error -> Result.Error(t)
                is Result.Loading -> Result.Loading(t)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading()
        )

    sealed class UIEvent {
        data object OnBack: UIEvent()
        data object OnRestore: UIEvent()
        data class TitleChanged(val newValue: String): UIEvent()
        data class DescriptionChanged(val newValue: String): UIEvent()
        data class PictureChanged(val newValue: Uri?): UIEvent()
        data class ModeChanged(val newValue: TourMode): UIEvent()
        data class WithPictureChanged(val newValue: WithHelper): UIEvent()
        data class WithHintChanged(val newValue: WithHelper): UIEvent()
        data class WithDelayChanged(val newValue: WithHelper): UIEvent()
        data object GuidesListOnCreate: UIEvent()
        data class GuidesListOnEdit(val newValue: FullGuide): UIEvent()
        data class GuidesListOnDelete(val newValue: FullGuide): UIEvent()
        data object GuidesListOnReorder : UIEvent()
    }

    private fun addGuide (list : List<FullGuide>, guide : FullGuide) : List<FullGuide> {
        return list + guide
    }

    private fun removeGuide (list : List<FullGuide>, guide : FullGuide) : List<FullGuide> {
        return list.filter { it.guide.guideId != guide.guide.guideId }
    }

    private fun validate (oldTour : ManagedTourWithGuides, uiEvent: UIEvent) : Boolean {
        val newTour : ManagedTourWithGuides = when (uiEvent) {
            is UIEvent.TitleChanged -> oldTour.copy(tour = oldTour.tour.copy(title = uiEvent.newValue))
            is UIEvent.DescriptionChanged -> oldTour.copy(tour = oldTour.tour.copy(description = uiEvent.newValue))
            is UIEvent.ModeChanged -> oldTour.copy(tour = oldTour.tour.copy(mode = uiEvent.newValue))
            is UIEvent.PictureChanged -> oldTour.copy(tour = oldTour.tour.copy(picture = uiEvent.newValue))
            is UIEvent.WithPictureChanged -> oldTour.copy(tour = oldTour.tour.copy(withPicture = uiEvent.newValue))
            is UIEvent.WithHintChanged -> oldTour.copy(tour = oldTour.tour.copy(withHint = uiEvent.newValue))
            is UIEvent.WithDelayChanged -> oldTour.copy(tour = oldTour.tour.copy(withDelay = uiEvent.newValue))
            UIEvent.GuidesListOnCreate -> oldTour.copy(guides = addGuide(oldTour.guides, FullGuide(Guide(), POI(0L, "",""), Trigger(0L, "","", "",""))))
            is UIEvent.GuidesListOnDelete -> oldTour.copy(guides = removeGuide(oldTour.guides, uiEvent.newValue))
            else -> return false
        }
        val validation = TourValidator(newTour).validateAll()
        val isValid = ! validation.hasError()
        return isValid
    }

    private suspend fun update (tour : ManagedTourWithGuides, uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.TitleChanged -> repository.update(Title(tour.tour.tourId, uiEvent.newValue))
            is UIEvent.DescriptionChanged -> repository.update(Description(tour.tour.tourId, uiEvent.newValue))
            is UIEvent.ModeChanged -> repository.update(Mode(tour.tour.tourId, uiEvent.newValue))
            is UIEvent.PictureChanged -> repository.update(Picture(tour.tour.tourId, uiEvent.newValue))
            is UIEvent.WithPictureChanged -> repository.update(WithPicture(tour.tour.tourId, uiEvent.newValue))
            is UIEvent.WithDelayChanged -> repository.update(WithDelay(tour.tour.tourId, uiEvent.newValue))
            is UIEvent.WithHintChanged -> repository.update(WithHint(tour.tour.tourId, uiEvent.newValue))
            is UIEvent.GuidesListOnDelete -> repository.deleteGuide(uiEvent.newValue)
            UIEvent.GuidesListOnReorder -> { }
            else -> return
        }
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        if (uiState.value !is Result.Success) return@launch
        val tour : ManagedTourWithGuides = (uiState.value as Result.Success<UIState>).content.tour
        when (uiEvent) {
            is UIEvent.OnBack -> navigator.navigateUp()
            is UIEvent.OnRestore -> repository.restore(tour.tour.tourId)
            UIEvent.GuidesListOnCreate -> navigator.navigate(
                destination = TourDestination.CreateGuideScreen(
                    tour.tour.tourId,
                    tour.guides.size+1,
                )
            )
            is UIEvent.GuidesListOnEdit -> navigator.navigate(
                destination = TourDestination.EditGuideScreen(uiEvent.newValue.guide.guideId)
            )
            UIEvent.GuidesListOnReorder -> repository.updateGuidePosition(tour.guides.map {Position(it.guide.guideId, it.guide.position)})
            else -> {
                update(tour, uiEvent)
                val isValid = validate(tour, uiEvent)
                repository.valid(tour.tour.tourId, isValid)
            }
        }
    }

    fun edit (tid : Long) = viewModelScope.launch {
        val tid : Long = repository.edit(tid)
        _id.value = tid
    }

    fun create(tour : Tour) = viewModelScope.launch {
        val tid : Long = repository.create(tour)
        _id.value = tid
    }

}
