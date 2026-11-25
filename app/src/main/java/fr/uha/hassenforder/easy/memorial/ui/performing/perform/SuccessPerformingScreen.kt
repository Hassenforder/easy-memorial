package fr.uha.hassenforder.easy.memorial.ui.performing.perform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.tab.TabItem
import fr.uha.hassenforder.easy.android.ui.tab.TabLayout
import fr.uha.hassenforder.easy.memorial.R

@Composable
fun SuccessPerformingScreen(
    uiState: PerformingViewModel.UIState,
    send : (PerformingViewModel.UIEvent) -> Unit,
) {
    Scaffold(
        topBar = { AppTopBar(uiState.uiTitle, { send (PerformingViewModel.UIEvent.OnBack) }) },
    ) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {
            InternalSuccessPerformingScreen(uiState, send)
        }
    }
}

@Composable
private fun InternalSuccessPerformingScreen(
    tour: PerformingViewModel.UIState,
    send : (PerformingViewModel.UIEvent) -> Unit,
) {
    val tabs = listOf(
        TabItem(
            titleId = R.string.action_perform,
            icon = Icons.Filled.Map,
            content = {
                PerformingProgressScreen(tour, send)
            }
        ),
        TabItem(
            titleId = R.string.action_guides,
            icon = Icons.Filled.Map,
            content = {
                PerformingGuidesScreen(tour, send)
            }
        ),
        TabItem(
            titleId = R.string.action_tour_description,
            icon = Icons.Filled.Description,
            content = {
                PerformingDescriptionScreen(tour, send)
            }
        ),
    )
    TabLayout(tabs)
}
