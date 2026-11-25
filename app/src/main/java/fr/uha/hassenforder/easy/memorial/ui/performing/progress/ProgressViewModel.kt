package fr.uha.hassenforder.easy.memorial.ui.performing.progress

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.app.UITitleBuilder
import fr.uha.hassenforder.easy.memorial.database.dao.TourProgressDao.TourProgressUpdate.Mode
import fr.uha.hassenforder.easy.memorial.database.dao.TourProgressDao.TourProgressUpdate.State
import fr.uha.hassenforder.easy.memorial.database.dao.TourProgressDao.TourProgressUpdate.WithDelay
import fr.uha.hassenforder.easy.memorial.database.dao.TourProgressDao.TourProgressUpdate.WithHint
import fr.uha.hassenforder.easy.memorial.database.dao.TourProgressDao.TourProgressUpdate.WithPicture
import fr.uha.hassenforder.easy.memorial.model.ProgressState
import fr.uha.hassenforder.easy.memorial.model.TourMode
import fr.uha.hassenforder.easy.memorial.model.TourProgress
import fr.uha.hassenforder.easy.memorial.model.WithHelper
import fr.uha.hassenforder.easy.memorial.navigation.PerformingDestination
import fr.uha.hassenforder.easy.memorial.repository.PerformingRepository
import fr.uha.hassenforder.easy.memorial.validation.ProgressValidationError
import fr.uha.hassenforder.easy.memorial.validation.ProgressValidator
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
class ProgressViewModel @Inject constructor (
    private val repository: PerformingRepository,
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
    private val _tourProgress: StateFlow<Result<TourProgress>> = _id
        .flatMapLatest { id -> repository.getTourProgressById(id) }
        .map {  progress ->
            if (progress != null) {
                Result.Success(content = progress)
            } else {
                Result.Error()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    @Immutable
    data class UIState(
        val uiTitle : UITitle,
        val progress: TourProgress,
        val validation: ProgressValidationError,
    ) {
        companion object {
            fun create (uiTitle: UITitle, progress: TourProgress) : UIState {
                val validation = ProgressValidator(progress).validateAll()
                return UIState(uiTitle, progress, validation)
            }
        }
    }

    val uiState : StateFlow<Result<UIState>> = combine(_uiTitle, _tourProgress)
        {   t, progress ->
            when (progress) {
                is Result.Success -> Result.Success(content = UIState.create(t, progress.content))
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
        data class StateChanged(val newValue: ProgressState): UIEvent()
        data class ModeChanged(val newValue: TourMode): UIEvent()
        data class WithPictureChanged(val newValue: WithHelper): UIEvent()
        data class WithHintChanged(val newValue: WithHelper): UIEvent()
        data class WithDelayChanged(val newValue: WithHelper): UIEvent()
    }

    private suspend fun update (progress: TourProgress, uiEvent: UIEvent) {
        when (uiEvent) {
            is UIEvent.StateChanged -> repository.update(State(progress.progressId, uiEvent.newValue))
            is UIEvent.ModeChanged -> repository.update(Mode(progress.progressId, uiEvent.newValue))
            is UIEvent.WithPictureChanged -> repository.update(WithPicture(progress.progressId, uiEvent.newValue))
            is UIEvent.WithHintChanged -> repository.update(WithHint(progress.progressId, uiEvent.newValue))
            is UIEvent.WithDelayChanged -> repository.update(WithDelay(progress.progressId, uiEvent.newValue))
            else -> return
        }
    }

    private fun validate (oldProgress: TourProgress, uiEvent: UIEvent) : Boolean {
        val newProgress : TourProgress = when (uiEvent) {
            is UIEvent.StateChanged -> oldProgress.copy(state = uiEvent.newValue)
            is UIEvent.ModeChanged -> oldProgress.copy(mode = uiEvent.newValue)
            is UIEvent.WithPictureChanged -> oldProgress.copy(withPicture = uiEvent.newValue)
            is UIEvent.WithHintChanged -> oldProgress.copy(withHint = uiEvent.newValue)
            is UIEvent.WithDelayChanged -> oldProgress.copy(withDelay = uiEvent.newValue)
            else -> return false
        }
        val validation = ProgressValidator(newProgress).validateAll()
        val isValid = ! validation.hasError()
        return isValid
    }

    fun send (uiEvent : UIEvent) = viewModelScope.launch {
        if (uiState.value !is Result.Success) return@launch
        val progress: TourProgress = (uiState.value as Result.Success<UIState>).content.progress
        when (uiEvent) {
            UIEvent.OnBack -> {
                if (progress.needConfiguration()) navigator.navigateUp()
                else {
                    navigator.navigateUp()
                    navigator.navigate(
                        PerformingDestination.PerformProgressScreen(progress.progressId),
/*                        navOptions = {
                            popUpTo(TourPerformingDestination.CreateProgressScreen) {
                                inclusive = true
                            }
                        }
*/                    )
                }
            }
            is UIEvent.OnRestore -> repository.restore(progress.progressId)
            else -> {
                update(progress, uiEvent)
                val isValid = validate(progress, uiEvent)
                repository.valid(progress.progressId, isValid)
            }
        }
    }

    fun delete (progress : TourProgress) = viewModelScope.launch {
        repository.delete(progress)
    }

    fun edit (pid : Long) = viewModelScope.launch {
        val pid : Long = repository.edit(pid)
        _id.value = pid
    }

    fun createUsingTour(tourId : Long) = viewModelScope.launch {
        val progressId : Long = repository.createUsingTour(tourId)
        _id.value = progressId
    }

}
