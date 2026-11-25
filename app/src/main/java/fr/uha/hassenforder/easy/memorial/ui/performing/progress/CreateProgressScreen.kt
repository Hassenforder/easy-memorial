package fr.uha.hassenforder.easy.memorial.ui.performing.progress

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.memorial.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun CreateProgressScreen (
    vm: ProgressViewModel = hiltViewModel(),
    tourId : Long
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_progress)

    LaunchedEffect(Unit) {
        vm.uiTitleBuilder.setScreenNameId(R.string.title_create)
        vm.uiTitleBuilder.setDefaultDocumentName(defaultDocumentName)
        withContext(Dispatchers.IO) {
            vm.createUsingTour(tourId)
        }
    }

    StateScreen(state = uiState) {
        if (it.progress.needConfiguration()) {
            InternalCreateTourProgressScreen(it, { vm.send(it) })
        } else {
            vm.send (ProgressViewModel.UIEvent.OnBack)
        }
    }

}

@Composable
private fun InternalCreateTourProgressScreen(
    uiState: ProgressViewModel.UIState,
    send : (ProgressViewModel.UIEvent) -> Unit,
) {
    Scaffold(
        topBar = { AppTopBar(uiState.uiTitle, { send (ProgressViewModel.UIEvent.OnBack) } ) },
    ) { innerPadding ->
        SuccessProgressScreen(uiState, send, Modifier.padding(innerPadding))
    }
}

