package fr.uha.hassenforder.easy.memorial.ui.performing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import fr.uha.hassenforder.easy.android.database.model.ListMode
import fr.uha.hassenforder.easy.android.ui.field.management.ManagementStateText
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.lazy.SwipeToRevealItem
import fr.uha.hassenforder.easy.android.ui.field.management.managementListModeMenuEntries
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.memorial.dto.ManagedTourPerforming

@Composable
fun ListPerformingScreen(
    vm: ListPerformingViewModel = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_progress)

    LaunchedEffect(Unit) {
        vm.titleBuilder.setScreenNameId(R.string.title_list)
        vm.titleBuilder.setDefaultDocumentName(defaultDocumentName)
    }

    StateScreen(uiState) {
        SuccessListScreen(it, { vm.send(it) })
    }

}

@Composable
private fun SuccessListScreen(
    uiState: ListPerformingViewModel.UIState,
    send : (ListPerformingViewModel.UIEvent) -> Unit,
) {
    val menuEntries = managementListModeMenuEntries(
        uiState.listMode,
        { send(ListPerformingViewModel.UIEvent.OnList(it)) }
    )

    Scaffold(
        topBar = { AppTopBar(uiState.uiTitle, { send (ListPerformingViewModel.UIEvent.OnBack) }, menuEntries) },
    ) { innerPadding ->
        InternalListScreen(uiState, send, Modifier.padding(innerPadding))
    }
}

@Composable
private fun InternalListScreen(
    uiState: ListPerformingViewModel.UIState,
    send : (ListPerformingViewModel.UIEvent) -> Unit,
    modifier: Modifier
) {
    val stateList = rememberLazyListState()

    LazyColumn (
        state = stateList,
        modifier = modifier
    ) {
        items(
            items = uiState.tours,
            key = { tour -> tour.tour.tourId },
        ) { item ->
            SwipeToRevealItem(
                onEdit = {
                    if (item.progress == null) {
                        send(ListPerformingViewModel.UIEvent.OnCreateProgress(item))
                    } else {
                        send(ListPerformingViewModel.UIEvent.OnEditProgress(item))
                    }
                },
                allowDelete = { item.progress != null },
                onDelete = { send (ListPerformingViewModel.UIEvent.OnDeleteProgress(item)) }
            ) {
                TourPerformingItem(item, uiState.listMode)
            }
        }
    }
}

@Composable
private fun TourPerformingItem (
    tour : ManagedTourPerforming,
    listMode : ListMode,
) {
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        leadingContent = {
            AsyncImage(
                model = tour.tour.picture,
                modifier = Modifier.size(64.dp),
                contentDescription = null,
                error = rememberVectorPainter(Icons.Outlined.Error),
                placeholder = rememberVectorPainter(Icons.Outlined.HourglassBottom),
            )
        },
        overlineContent = {
            Row (
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (tour.progress == null) {
                    Text(stringResource(id = R.string.title_tour_not_started), modifier = Modifier.weight(1f))
                } else {
                    Text(stringResource(id = R.string.title_tour_is_started))
                    Text(tour.management!!.state.name, modifier = Modifier.weight(1f))
                    ManagementStateText(tour.management!!.state, listMode)
                }
            }
        },
        supportingContent = {
            Text(tour.tour.description, maxLines = 3)
        },
        content = {
            Text(tour.tour.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        },
    )
}
