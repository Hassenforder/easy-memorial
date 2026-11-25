package fr.uha.hassenforder.easy.memorial.ui.performing.perform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import fr.uha.hassenforder.easy.android.ui.dialog.OutlinedDialog
import fr.uha.hassenforder.easy.android.ui.field.video.VideoPlayer
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.memorial.dto.GuidePerforming
import fr.uha.hassenforder.easy.memorial.icons.outlined.Candle
import fr.uha.hassenforder.easy.memorial.model.ProgressState

@Composable
fun PerformingGuidesScreen(
    uiState: PerformingViewModel.UIState,
    send : (PerformingViewModel.UIEvent) -> Unit,
) {
    LazyColumn {
        items(
            uiState.guides.sortedBy { item -> item.getOrder() },
            key = { item: GuidePerforming -> item.guide.guide.guideId },
        ) { item ->
            GuideWithPoiItem(item, send)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FullGuideDialog (
    guide : GuidePerforming,
    onClose: () -> Unit,
) {
    OutlinedDialog(onDismiss = { onClose() }, titleId = R.string.title_found) {
        Text(
            guide.guide.poi.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow. Ellipsis,
        )
        AsyncImage(
            model = guide.guide.poi.picture,
            modifier = Modifier.width(128.dp),
            contentDescription = null,
            error = rememberVectorPainter(Icons.Outlined.Candle),
            placeholder = rememberVectorPainter(Icons.Outlined.HourglassBottom),
        )
        Text(guide.guide.poi.description)
        VideoPlayer(uri = guide.guide.poi.media, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
private fun GuideWithPoiItem(
    item: GuidePerforming,
    send : (PerformingViewModel.UIEvent) -> Unit,
) {
    val showPopup = remember { mutableStateOf(false) }
    if (showPopup.value) {
        FullGuideDialog(
            item,
            onClose = { showPopup.value = false },
        )
    }
    ListItem(
        modifier = Modifier.fillMaxWidth(),
        leadingContent = {
            AsyncImage(
                model = item.guide.poi.picture,
                modifier = Modifier.size(64.dp),
                contentDescription = null,
                error = rememberVectorPainter(Icons.Outlined.Candle),
                placeholder = rememberVectorPainter(Icons.Outlined.HourglassBottom),
            )
        },
        trailingContent = {
            if (item.progress.state == ProgressState.DONE) {
                Button(onClick = { showPopup.value = true }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "opens"
                    )
                }
            }
        },
        overlineContent = {
            Text(item.guide.poi.name)
        },
        supportingContent = {
            if (item.progress.state == ProgressState.DONE) {
                Text(item.guide.poi.description, maxLines = 3)
            } else {
                Text(item.guide.trigger.label)
            }
        },
        content = {
            Text(
                modifier = Modifier.clickable {
                    send (PerformingViewModel.UIEvent.GuideNextState(item))
                },
                text = item.progress.state.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        },
    )
}
