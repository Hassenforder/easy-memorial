package fr.uha.hassenforder.easy.memorial.ui.performing.perform

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.app.UITitleBuilder
import fr.uha.hassenforder.easy.memorial.dto.FullTourPerforming
import fr.uha.hassenforder.easy.memorial.dto.GuidePerforming
import fr.uha.hassenforder.easy.memorial.model.ProgressState
import fr.uha.hassenforder.easy.memorial.model.Tour
import fr.uha.hassenforder.easy.memorial.model.TourProgress
import fr.uha.hassenforder.easy.memorial.repository.PerformingRepository
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
class PerformingViewModel @Inject constructor (
    private val repository: PerformingRepository,
    private val navigator: Navigator,
): ViewModel() {

    val titleBuilder = UITitleBuilder()

    private val _uiTitle : StateFlow<UITitle> = titleBuilder.uiTitle.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UITitle()
    )

    private val _id: MutableStateFlow<Long> = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _performing: StateFlow<Result<FullTourPerforming>> = _id
        .flatMapLatest { id -> repository.getFullTourPerforming(id) }
        .map { performing ->
            if (performing != null) {
                Result.Success(content = performing)
            } else {
                Result.Error()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Result.Loading())

    @Immutable
    data class UIState(
        val uiTitle: UITitle,
        val progress : TourProgress,
        val tour : Tour,
        val guides : List<GuidePerforming>,
        val performing : FullTourPerforming,
    ) {
        companion object {
            fun create (uiTitle: UITitle, performing : FullTourPerforming) : UIState {
                val progress = performing.progress
                val tour = performing.tour
                val guides = performing.guides
                return UIState(uiTitle, progress, tour, guides, performing)
            }
        }
    }

    val uiState : StateFlow<Result<UIState>> = combine(_uiTitle, _performing)
        { t, p ->
            when (p) {
                is Result.Success -> Result.Success(content = UIState.create(t, p.content))
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
        data class GuideNextState(val guide: GuidePerforming): UIEvent()
        data class GuideDoneState(val guide: GuidePerforming): UIEvent()
    }

    fun send (uiEvent : UIEvent) {
        viewModelScope.launch {
            when (uiEvent) {
                UIEvent.OnBack -> navigator.navigateUp()
                is UIEvent.GuideNextState -> {
                    val currentIndex = uiEvent.guide.progress.state.ordinal
                    val nextIndex = if (currentIndex == ProgressState.entries.size -1) 0 else currentIndex+1
                    val progress = uiEvent.guide.progress.copy (state = ProgressState.entries[nextIndex])
                    repository.updateGuideProgress(progress)
                }
                is UIEvent.GuideDoneState -> {
                    val progress = uiEvent.guide.progress.copy (state = ProgressState.DONE)
                    repository.updateGuideProgress(progress)
                }
            }
        }
    }

    fun edit (pid : Long) = viewModelScope.launch {
        _id.value = pid
    }

}
