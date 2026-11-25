package fr.uha.hassenforder.easy.memorial.ui.tour

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.field.management.managementRestoreMenuEntries
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.android.ui.tab.TabItem
import fr.uha.hassenforder.easy.android.ui.tab.TabLayout

@Composable
fun SuccessTourScreen(
    uiState: TourViewModel.UIState,
    send : (TourViewModel.UIEvent) -> Unit,
) {
    val menuEntries = managementRestoreMenuEntries(uiState.tour.management, { send(TourViewModel.UIEvent.OnRestore) })
    Scaffold(
        topBar = { AppTopBar( uiState.uiTitle, { send(TourViewModel.UIEvent.OnBack) }, menuEntries ) },
    ) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {
            TabbedEditTourScreen(uiState, send)
        }
    }
}

@Composable
private fun TabbedEditTourScreen(
    uiState: TourViewModel.UIState,
    send : (TourViewModel.UIEvent) -> Unit,
) {
    val tabs = listOf(
        TabItem(
            titleId = R.string.action_tour_description,
            icon = Icons.Filled.Description,
            content = {
                TourDescriptionScreen (uiState, send)
            }
        ),
        TabItem(
            titleId = R.string.action_run,
            icon = Icons.Filled.Start,
            content = {
                TourRunScreen (uiState, send)
            }
        ),
        TabItem(
            titleId = R.string.action_guides,
            icon = Icons.Filled.Map,
            content = {
                TourGuideScreen(uiState,send)
            }
        ),
    )
    TabLayout(tabs)
}
