package fr.uha.hassenforder.easy.memorial.ui.tour.guide

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.memorial.R

@Composable
fun EditGuideScreen (
    vm: GuideViewModel = hiltViewModel(),
    gid : Long
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_guide)

    LaunchedEffect(Unit) {
        vm.uiTitleBuilder.setScreenNameId(R.string.title_edit)
        vm.uiTitleBuilder.setDefaultDocumentName(defaultDocumentName)
        vm.edit(gid)
    }

    StateScreen(state = uiState) {
        SuccessGuideScreen(it, { vm.send(it) })
    }

}
