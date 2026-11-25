package fr.uha.hassenforder.easy.memorial.ui.tour

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.memorial.model.Tour
import fr.uha.hassenforder.easy.memorial.model.TourMode
import fr.uha.hassenforder.easy.memorial.model.WithHelper

@Composable
fun CreateTourScreen (
    vm: TourViewModel = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_tour)

    LaunchedEffect(Unit) {
        vm.uiTitleBuilder.setScreenNameId(R.string.title_create)
        vm.uiTitleBuilder.setDefaultDocumentName(defaultDocumentName)
        val tour = Tour (tourId = 0, title ="", description="", picture=null,
            mode = TourMode.USER,
            withPicture = WithHelper.USER, withHint = WithHelper.USER, withDelay = WithHelper.USER
        )
        vm.create(tour)
    }

    StateScreen(state = uiState) {
        SuccessTourScreen(it, { vm.send(it) })
    }

}
