package fr.uha.hassenforder.easy.memorial.ui.performing

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.database.model.ListMode
import fr.uha.hassenforder.easy.android.database.model.ManagementState
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.android.navigation.Navigator
import fr.uha.hassenforder.easy.android.ui.app.UITitle
import fr.uha.hassenforder.easy.android.ui.app.UITitleBuilder
import fr.uha.hassenforder.easy.memorial.dto.ManagedTourPerforming
import fr.uha.hassenforder.easy.memorial.navigation.PerformingDestination
import fr.uha.hassenforder.easy.memorial.repository.PerformingRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
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
class ListPerformingViewModel @Inject constructor (
    private val repository: PerformingRepository,
    private val navigator: Navigator
): ViewModel() {

    val titleBuilder = UITitleBuilder()

    private val _uiTitle : StateFlow<UITitle> = titleBuilder.uiTitle.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UITitle()
    )

    private val _listMode = MutableStateFlow(ListMode.FILTERED)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _tours: Flow<Result<List<ManagedTourPerforming>>> = _listMode
        .flatMapLatest { m ->
            when(m) {
                ListMode.ALL -> repository.getAll()
                ListMode.FILTERED -> repository.getFiltered()
            }
        }
        .map { content -> Result.Success(content) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading()
        )

    @Immutable
    data class UIState (
        val uiTitle : UITitle,
        val listMode : ListMode,
        val tours : List<ManagedTourPerforming>,
    )

    val uiState : StateFlow<Result<UIState>> = combine(_uiTitle, _listMode, _tours)
        { t, mode, list ->
            when (list) {
                is Result.Success -> Result.Success(content = UIState(t, mode, list.content))
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
        data class OnList(val newValue: ListMode): UIEvent()
        data class OnCreateProgress(val newValue: ManagedTourPerforming): UIEvent()
        data class OnEditProgress(val newValue: ManagedTourPerforming): UIEvent()
        data class OnDeleteProgress(val newValue: ManagedTourPerforming): UIEvent()
    }

    fun send (uiEvent : UIEvent) {
        viewModelScope.launch {
            when (uiEvent) {
                is UIEvent.OnBack -> navigator.navigateUp()
                is UIEvent.OnList -> _listMode.value = uiEvent.newValue
                is UIEvent.OnCreateProgress -> {
                    if (uiEvent.newValue.progress != null) return@launch
                    navigator.navigate(
                        destination = PerformingDestination.CreateProgressScreen(uiEvent.newValue.tour.tourId),
                    )
                }
                is UIEvent.OnEditProgress -> {
                    if (uiEvent.newValue.progress == null) return@launch
                    if (uiEvent.newValue.management == null) return@launch
                    when(uiEvent.newValue.management.state) {
                        ManagementState.CREATED, ManagementState.BACKUP -> { }
                        ManagementState.HIDDEN ->
                            repository.show(uiEvent.newValue.progress.progressId)
                        ManagementState.VALID, ManagementState.INVALID ->
                            navigator.navigate(
                                destination = PerformingDestination.PerformProgressScreen(uiEvent.newValue.progress.progressId),
                            )
                    }
                }
                is UIEvent.OnDeleteProgress -> {
                    if (uiEvent.newValue.progress == null) return@launch
                    if (uiEvent.newValue.management == null) return@launch
                    when(uiEvent.newValue.management.state) {
                        ManagementState.CREATED, ManagementState.BACKUP, ManagementState.HIDDEN ->
                            repository.delete(uiEvent.newValue.progress)
                        ManagementState.VALID, ManagementState.INVALID ->
                            repository.hide(uiEvent.newValue.tour.tourId)
                    }
                }
            }
        }
    }

}