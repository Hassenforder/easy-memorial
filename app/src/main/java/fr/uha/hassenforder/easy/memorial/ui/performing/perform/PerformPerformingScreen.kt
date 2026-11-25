package fr.uha.hassenforder.easy.memorial.ui.performing.perform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.memorial.R

@Composable
fun PerformPerformingScreen (
    vm: PerformingViewModel = hiltViewModel(),
    progressId : Long
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_performing)

    LaunchedEffect(Unit) {
        vm.titleBuilder.setScreenNameId(R.string.title_perform)
        vm.titleBuilder.setDefaultDocumentName(defaultDocumentName)
        vm.edit(progressId)
    }

    StateScreen(state = uiState) {
        SuccessPerformingScreen(it, { vm.send(it) })
    }

}
