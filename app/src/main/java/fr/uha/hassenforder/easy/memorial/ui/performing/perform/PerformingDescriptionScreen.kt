package fr.uha.hassenforder.easy.memorial.ui.performing.perform

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.uha.hassenforder.easy.memorial.icons.outlined.Candle

@Composable
fun PerformingDescriptionScreen(
    uiState: PerformingViewModel.UIState,
    send : (PerformingViewModel.UIEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = uiState.tour.title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.tour.picture != null) {
            AsyncImage(
                model = uiState.tour.picture,
                modifier = Modifier.width(192.dp),
                contentDescription = "tour image",
                error = rememberVectorPainter(Icons.Outlined.Candle),
                placeholder = rememberVectorPainter(Icons.Outlined.HourglassBottom),
                contentScale = ContentScale.Fit
            )
        }
        Text(text = uiState.tour.description, modifier = Modifier.fillMaxWidth())
    }
}