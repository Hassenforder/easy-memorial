package fr.uha.hassenforder.easy.memorial.ui.tour

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
import fr.uha.hassenforder.easy.memorial.dto.ManagedTour
import fr.uha.hassenforder.easy.memorial.navigation.TourDestination
import fr.uha.hassenforder.easy.memorial.repository.TourRepository
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
class ListToursViewModel @Inject constructor (
    private val repository: TourRepository,
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
    private val _tours: Flow<Result<List<ManagedTour>>> = _listMode
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
        val tours : List<ManagedTour>,
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
        data class  OnList(val newValue: ListMode): UIEvent()
        data object OnCreate: UIEvent()
        data class  OnEdit(val newValue: ManagedTour): UIEvent()
        data class  OnDelete(val newValue: ManagedTour): UIEvent()
        data class  OnSwap(val from: Int, val to: Int): UIEvent()
    }

    fun send (uiEvent : UIEvent) {
        viewModelScope.launch {
            when (uiEvent) {
                is UIEvent.OnBack -> navigator.navigateUp()
                is UIEvent.OnList -> _listMode.value = uiEvent.newValue
                is UIEvent.OnCreate -> navigator.navigate(
                    destination = TourDestination.CreateScreen,
                )
                is UIEvent.OnEdit ->
                    when(uiEvent.newValue.management.state) {
                        ManagementState.CREATED, ManagementState.BACKUP -> { }
                        ManagementState.HIDDEN ->
                            repository.show(uiEvent.newValue.tour.tourId)
                        ManagementState.VALID, ManagementState.INVALID ->
                            navigator.navigate(
                                destination = TourDestination.EditScreen(uiEvent.newValue.tour.tourId),
                            )
                    }
                is UIEvent.OnDelete ->
                    when(uiEvent.newValue.management.state) {
                        ManagementState.CREATED, ManagementState.BACKUP, ManagementState.HIDDEN ->
                            repository.delete(uiEvent.newValue.tour)
                        ManagementState.VALID, ManagementState.INVALID ->
                            repository.hide(uiEvent.newValue.tour.tourId)
                    }
                is UIEvent.OnSwap -> swap(uiEvent.from, uiEvent.to)
            }
        }
    }

    private fun swap(from: Int, to: Int) = viewModelScope.launch {
        if (uiState.value !is Result.Success) return@launch
    }

}