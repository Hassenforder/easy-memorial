package fr.uha.hassenforder.easy.memorial.ui.tour.guide

import android.net.Uri
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.app.UITitleBuilder
import fr.uha.hassenforder.easy.memorial.database.dao.GuideDao.GuideUpdate.*
import fr.uha.hassenforder.easy.memorial.database.dao.PoiDao.PoiUpdate.*
import fr.uha.hassenforder.easy.memorial.database.dao.TriggerDao.TriggerUpdate.*
import fr.uha.hassenforder.easy.memorial.dto.FullGuide
import fr.uha.hassenforder.easy.memorial.repository.GuideRepository
import fr.uha.hassenforder.easy.memorial.validation.GuideValidationError
import fr.uha.hassenforder.easy.memorial.validation.GuideValidator
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

@HiltViewModel
class GuideViewModel @Inject constructor (
    private val repository: GuideRepository,
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
    private val _guide: StateFlow<Result<FullGuide>> = _id
        .flatMapLatest { id -> repository.getById(id) }
        .map {
            guide -> if (guide != null) {
                Result.Success(content = guide)
            } else {
                Result.Error()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    @Immutable
    data class UIState(
        val uiTitle : UITitle,
        val guide : FullGuide,
        val validation : GuideValidationError,
    ) {
        companion object {
            fun create (uiTitle: UITitle, guide : FullGuide) : UIState {
                val validation = GuideValidator(guide).validateAll()
                return UIState(uiTitle, guide, validation)
            }
        }
    }

    val uiState : StateFlow<Result<UIState>> = combine(_uiTitle, _guide)
        { t, g ->
            when (g) {
                is Result.Success -> Result.Success(content = UIState.create(t, g.content))
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
        data class PositionChanged(val newValue: Int): UIEvent()
        data class TitleChanged(val newValue: String): UIEvent()
        data class DescriptionChanged(val newValue: String): UIEvent()
        data class PictureChanged(val newValue: Uri?): UIEvent()
        data class MediaChanged(val newValue: Uri?): UIEvent()
        data class LabelChanged(val newValue: String): UIEvent()
        data class LatitudeChanged(val newValue: String): UIEvent()
        data class LongitudeChanged(val newValue: String): UIEvent()
        data class HintChanged(val newValue: String): UIEvent()
    }

    fun validate (oldGuide : FullGuide, uiEvent : UIEvent) : Boolean {
        val newGuide : FullGuide = when (uiEvent) {
            is UIEvent.PositionChanged -> oldGuide.copy(guide = oldGuide.guide.copy(position = uiEvent.newValue))
            is UIEvent.TitleChanged -> oldGuide.copy(poi = oldGuide.poi.copy(name = uiEvent.newValue))
            is UIEvent.DescriptionChanged -> oldGuide.copy(poi = oldGuide.poi.copy(description = uiEvent.newValue))
            is UIEvent.PictureChanged -> oldGuide.copy(poi = oldGuide.poi.copy(picture = uiEvent.newValue))
            is UIEvent.MediaChanged -> oldGuide.copy(poi = oldGuide.poi.copy(media = uiEvent.newValue))
            is UIEvent.LabelChanged -> oldGuide.copy(trigger = oldGuide.trigger.copy(label = uiEvent.newValue))
            is UIEvent.LatitudeChanged -> oldGuide.copy(trigger = oldGuide.trigger.copy(latitude = uiEvent.newValue))
            is UIEvent.LongitudeChanged -> oldGuide.copy(trigger = oldGuide.trigger.copy(longitude = uiEvent.newValue))
            is UIEvent.HintChanged -> oldGuide.copy(trigger = oldGuide.trigger.copy(hint = uiEvent.newValue))
            else -> return false
        }
        val validation = GuideValidator(newGuide).validateAll()
        val isValid = ! validation.hasError()
        return isValid
    }

    suspend fun update (guide : FullGuide, uiEvent : UIEvent) {
        when (uiEvent) {
            is UIEvent.PositionChanged -> repository.update(Position(guide.guide.guideId, uiEvent.newValue))
            is UIEvent.TitleChanged -> repository.update(Name(guide.poi.poiId, uiEvent.newValue))
            is UIEvent.DescriptionChanged -> repository.update(Description(guide.poi.poiId, uiEvent.newValue))
            is UIEvent.PictureChanged -> repository.update(Picture(guide.poi.poiId, uiEvent.newValue))
            is UIEvent.MediaChanged -> repository.update(Media(guide.poi.poiId, uiEvent.newValue))
            is UIEvent.LabelChanged -> repository.update(Label(guide.trigger.triggerId, uiEvent.newValue))
            is UIEvent.LatitudeChanged -> repository.update(Latitude(guide.trigger.triggerId, uiEvent.newValue))
            is UIEvent.LongitudeChanged -> repository.update(Longitude(guide.trigger.triggerId, uiEvent.newValue))
            is UIEvent.HintChanged -> repository.update(Hint(guide.trigger.triggerId, uiEvent.newValue))
            UIEvent.OnBack -> { }
        }
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        if (uiState.value !is Result.Success) return@launch
        val guide : FullGuide = (uiState.value as Result.Success<UIState>).content.guide
        when (uiEvent) {
            is UIEvent.OnBack -> navigator.navigateUp()
            else -> {
                update(guide, uiEvent)
                val isValid = validate(guide, uiEvent)
//                repository.valid(guide.guide.guideId, isValid)
            }
        }
    }

    fun edit (pid : Long) = viewModelScope.launch {
        _id.value = pid
    }

    fun create(guide : FullGuide) = viewModelScope.launch {
        val gid : Long = repository.create(guide)
        _id.value = gid
    }

}
