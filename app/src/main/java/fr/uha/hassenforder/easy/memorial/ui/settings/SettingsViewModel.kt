package fr.uha.hassenforder.easy.memorial.ui.settings

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.hassenforder.easy.android.flow.Result
import fr.uha.hassenforder.easy.memorial.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor (
    private val repository: SettingsRepository
): ViewModel() {

    private val _filenames: Flow<List<String>> = repository.getAll()

    @Immutable
    data class UIState (
        val filenames : List<String>
    )

    val uiState : StateFlow<Result<UIState>> = _filenames
        .map { t -> Result.Success(UIState(t)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Result.Loading()
        )

    sealed class UIEvent {
        data object OnClear: UIEvent()
        data object OnPopulateByCode: UIEvent()
        data class OnPopulateByFile(val filename : String): UIEvent()
    }

    fun send (uiEvent : UIEvent) {
        viewModelScope.launch {
            when (uiEvent) {
                is UIEvent.OnClear -> clearDatabase()
                is UIEvent.OnPopulateByCode -> populateDatabase()
                is UIEvent.OnPopulateByFile -> populateDatabase(uiEvent.filename)
            }
        }
    }

    private fun populateDatabase () = viewModelScope.launch {
        repository.populate()
    }

    private fun populateDatabase (filename : String) = viewModelScope.launch {
        repository.populate(filename)
    }

    private fun clearDatabase () = viewModelScope.launch {
        repository.clear()
    }

}