package fr.uha.hassenforder.easy.memorial.ui.tour

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import fr.uha.hassenforder.easy.android.database.model.ListMode
import fr.uha.hassenforder.easy.android.icons.filled.NumberOne
import fr.uha.hassenforder.easy.android.ui.field.management.ManagementStateText
import fr.uha.hassenforder.easy.android.ui.screen.StateScreen
import fr.uha.hassenforder.easy.android.ui.app.AppTopBar
import fr.uha.hassenforder.easy.android.ui.lazy.SwipeToRevealItem
import fr.uha.hassenforder.easy.android.ui.field.management.managementListModeMenuEntries
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.memorial.dto.ManagedTour
import fr.uha.hassenforder.easy.memorial.model.TourMode
import fr.uha.hassenforder.easy.memorial.model.WithHelper
import fr.uha.hassenforder.easy.android.ui.lazy.dragContainer
import fr.uha.hassenforder.easy.android.ui.lazy.draggableItems
import fr.uha.hassenforder.easy.android.ui.lazy.rememberDragDropState
import fr.uha.hassenforder.easy.memorial.icons.outlined.Candle

@Composable
fun ListToursScreen(
    vm: ListToursViewModel = hiltViewModel(),
) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val defaultDocumentName = stringResource(id = R.string.title_tours)

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
    uiState: ListToursViewModel.UIState,
    send : (ListToursViewModel.UIEvent) -> Unit,
) {
    val menuEntries = managementListModeMenuEntries(
        uiState.listMode,
        { send(ListToursViewModel.UIEvent.OnList(it)) }
    )

    Scaffold(
        topBar = { AppTopBar(uiState.uiTitle, { send (ListToursViewModel.UIEvent.OnBack) }, menuEntries) },
        floatingActionButton = {
            FloatingActionButton(onClick = { send(ListToursViewModel.UIEvent.OnCreate) }) {
                Icon(Icons.Filled.Add, contentDescription = "add")
            }
        }
    ) { innerPadding ->
        InternalListScreen(uiState, send, Modifier.padding(innerPadding))
    }
}

@Composable
private fun InternalListScreen (
    uiState: ListToursViewModel.UIState,
    send : (ListToursViewModel.UIEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
//    var list by remember { mutableStateOf(uiState.tours) }
    val draggableItems by remember {
        derivedStateOf { uiState.tours.size }
    }
    val stateList = rememberLazyListState()

    val dragDropState =
        rememberDragDropState(
            lazyListState = stateList,
            draggableItemsNum = draggableItems,
            onMove = { fromIndex: Int, toIndex: Int ->
//                list = list.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
            }
        )
    LazyColumn(
        modifier = modifier.dragContainer(dragDropState),
        state = stateList,
    ) {
        draggableItems(
            items = uiState.tours,
            key = { index, tour -> tour.tour.tourId },
            dragDropState = dragDropState,
        ) { modifier, item ->
            SwipeToRevealItem(
                modifier = modifier,
                onEdit = { send(ListToursViewModel.UIEvent.OnEdit (item)) },
                onDelete = { send (ListToursViewModel.UIEvent.OnDelete(item)) }
            ) {
                TourItem(item, uiState.listMode, modifier)
            }
        }
    }
}

@Composable
private fun TourItem (
    tour : ManagedTour,
    listMode : ListMode,
    modifier : Modifier = Modifier,
) {
    ListItem(
        modifier = modifier,
        leadingContent = {
            AsyncImage(
                model = tour.tour.picture,
                modifier = Modifier.size(64.dp),
                contentDescription = null,
                error = rememberVectorPainter(Icons.Outlined.Candle),
                placeholder = rememberVectorPainter(Icons.Outlined.HourglassBottom),
            )
        },
        trailingContent = {
            Column {
                ModeIcon(tour.tour.mode)
                WithText(tour.tour.withPicture, "P")
                WithText(tour.tour.withHint, "T")
                WithText(tour.tour.withDelay, "D")
            }
        },
        supportingContent = {
            Text(tour.tour.description, maxLines = 3)
        },
        content = {
            Row {
                Text(tour.tour.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                ManagementStateText(tour.management.state, listMode)
            }
        },
    )
}

@Composable
private fun ModeIcon (mode : TourMode) {
    val icon : ImageVector = when(mode) {
        TourMode.LOCKED_FREE -> Icons.Filled.Circle
        TourMode.LOCKED_SEQUENCE -> Icons.Filled.NumberOne
        TourMode.USER -> Icons.Filled.Person
        TourMode.USER_FREE -> Icons.Filled.Circle
        TourMode.USER_SEQUENCE -> Icons.Filled.NumberOne
    }
    Icon(icon, contentDescription = "mode")
}

@Composable
private fun WithText (helper : WithHelper, suffix : String) {
    val with : String = when(helper) {
        WithHelper.LOCKED_YES -> " "
        WithHelper.LOCKED_NO -> "~"
        WithHelper.USER -> "?"
        WithHelper.USER_YES -> " "
        WithHelper.USER_NO -> "~"
    }
    Text(with+suffix)
}

