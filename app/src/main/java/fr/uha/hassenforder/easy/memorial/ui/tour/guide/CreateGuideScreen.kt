package fr.uha.hassenforder.easy.memorial.ui.tour.guide

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.memorial.dto.FullGuide
import fr.uha.hassenforder.easy.memorial.model.Guide
import fr.uha.hassenforder.easy.memorial.model.POI
import fr.uha.hassenforder.easy.memorial.model.Trigger

@Composable
fun CreateGuideScreen (
    vm: GuideViewModel = hiltViewModel(),
    tourId: Long,
    position: Int,
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_guide)

    LaunchedEffect(Unit) {
        vm.uiTitleBuilder.setScreenNameId(R.string.title_create)
        vm.uiTitleBuilder.setDefaultDocumentName(defaultDocumentName)
        val guide = FullGuide (
            Guide(guideId = 0, tourId = tourId, poiId = 0, triggerId = 0, position = position),
            POI(poiId = 0, name="",description="", picture=null, media=null),
            Trigger(triggerId = 0, label="", latitude = "", longitude = "", hint=""),
        )
        vm.create(guide)
    }

    StateScreen(state = uiState) {
        SuccessGuideScreen(it, { vm.send(it) })
    }

}
