package fr.uha.hassenforder.easy.memorial.ui.tour.guide

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.android.ui.tab.TabItem
import kotlinx.coroutines.launch

@Composable
fun SuccessGuideScreen(
    uiState: GuideViewModel.UIState,
    send : (GuideViewModel.UIEvent) -> Unit,
) {
    Scaffold(
        topBar = { AppTopBar( uiState.uiTitle, { send(GuideViewModel.UIEvent.OnBack) } ) },
    ) { innerPadding ->
        Column (
            modifier = Modifier.padding(innerPadding)
        ) {
            TabbedGuideScreen(uiState, send)
        }
    }
}

@Composable
private fun TabbedGuideScreen(
    uiState: GuideViewModel.UIState,
    send : (GuideViewModel.UIEvent) -> Unit,
) {
    val tabs = listOf(
        TabItem(
            titleId = R.string.action_tour_description,
            icon = Icons.Filled.Description,
            content = {
                GuideDescriptionScreen (uiState, send)
            }
        ),
        TabItem(
            titleId = R.string.action_trigger,
            icon = Icons.Filled.Map,
            content = {
                GuideTriggerScreen (uiState, send)
            }
        ),
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { tabs.size }
    )
    val coroutineScope = rememberCoroutineScope()

    SecondaryTabRow(
        selectedTabIndex = pagerState.currentPage,
    ) {
        tabs.forEachIndexed { index, item ->
            Tab(
                selected = index == pagerState.currentPage,
                text = { Text(text = stringResource(id = item.titleId)) },
                icon = { Icon(item.icon,  null) },
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
            )
        }
    }
    HorizontalPager(
        state = pagerState,
        pageSpacing = 1.dp
    ) {
        tabs[pagerState.currentPage].content()
    }
}

