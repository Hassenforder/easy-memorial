package fr.uha.hassenforder.easy.memorial.ui.tour

import android.annotation.SuppressLint
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.uha.hassenforder.easy.android.ui.lazy.SwipeToRevealItem
import fr.uha.hassenforder.easy.android.ui.lazy.SwipeableItem
import fr.uha.hassenforder.easy.android.ui.lazy.ReorderHapticFeedbackType
import fr.uha.hassenforder.easy.android.ui.lazy.ReorderableLazyColumn
import fr.uha.hassenforder.easy.android.ui.lazy.rememberReorderHapticFeedback
import fr.uha.hassenforder.easy.memorial.dto.FullGuide
import fr.uha.hassenforder.easy.memorial.icons.outlined.Candle
import sh.calvin.reorderable.ReorderableCollectionItemScope

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TourGuideScreen(
    uiState: TourViewModel.UIState,
    send : (TourViewModel.UIEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(onClick = { send(TourViewModel.UIEvent.GuidesListOnCreate) }) {
                Icon(Icons.Filled.Add, contentDescription = "add")
            }
        }
    ) { innerPadding ->
        ReorderableLazyColumn(
            items = uiState.tour.guides,
            key = { index : Int, item : FullGuide -> item.guide.guideId },
//            modifier = Modifier.padding(innerPadding),
            content = { scope, item ->
                SwipeToRevealItem(
                    modifier = Modifier.fillMaxWidth(),
                    onEdit = { send(TourViewModel.UIEvent.GuidesListOnEdit(item)) },
                    onDelete = { send (TourViewModel.UIEvent.GuidesListOnDelete(item)) },
                ) {
                    GuideWithPoiItem(
                        scope,
                        item,
                        onSwap = { send (TourViewModel.UIEvent.GuidesListOnReorder) }
                    )
                }
            },
        )
    }
}

@Composable
fun SwipeableGuideWithPoiItem (
    scope: ReorderableCollectionItemScope,
    item: FullGuide,
    onEdit : ((item : FullGuide) -> Unit)? = null,
    onDelete : ((item : FullGuide) -> Unit)? = null,
    onSwap : (() -> Unit)? = null,
) {
    SwipeableItem(
        modifier = Modifier.fillMaxWidth(),
        onEdit = { if (onEdit != null) { onEdit(item) } },
        onDelete = { if (onDelete != null) { onDelete(item) } }
    ) {
        GuideWithPoiItem(scope, item, onSwap = onSwap?: { })
    }
}

@Composable
private fun GuideWithPoiItem(
    scope: ReorderableCollectionItemScope,
    item: FullGuide,
    onSwap : () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val haptic = rememberReorderHapticFeedback()
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        leadingContent = {
            Row {
                Text(item.guide.position.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                if (item.poi.picture != null) {
                    AsyncImage(
                        model = item.poi.picture,
                        modifier = Modifier.size(64.dp),
                        contentDescription = null,
                        error = rememberVectorPainter(Icons.Outlined.Candle),
                        placeholder = rememberVectorPainter(Icons.Outlined.HourglassBottom),
                    )
                }
            }
        },
        trailingContent = {
            IconButton(
                modifier = with(scope) {
                    Modifier
                        .draggableHandle(
                            onDragStarted = {
                                haptic.performHapticFeedback(ReorderHapticFeedbackType.START)
                            },
                            onDragStopped = {
                                haptic.performHapticFeedback(ReorderHapticFeedbackType.END)
                                onSwap()
                            },
                            interactionSource = interactionSource,
                        )
                        .clearAndSetSemantics { }
                },
                onClick = {},
            ) {
                Icon(Icons.Rounded.DragHandle, contentDescription = "Reorder")
            }
        },
        supportingContent = {
            Text(item.poi.description, maxLines = 3)
        },
        content = {
            Text(item.poi.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        },
    )
}
