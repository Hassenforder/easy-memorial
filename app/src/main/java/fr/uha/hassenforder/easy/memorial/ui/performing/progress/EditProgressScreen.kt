package fr.uha.hassenforder.easy.memorial.ui.performing.progress

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.memorial.R

@Composable
fun EditProgressScreen (
    vm: ProgressViewModel = hiltViewModel(),
    progressId : Long
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_progress)

    LaunchedEffect(Unit) {
        vm.uiTitleBuilder.setScreenNameId(R.string.title_edit)
        vm.uiTitleBuilder.setDefaultDocumentName(defaultDocumentName)
        vm.edit(progressId)
    }

    StateScreen(state = uiState) {
        SuccessProgressScreen(it, { vm.send(it) })
    }

}