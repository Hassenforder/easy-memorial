package fr.uha.hassenforder.easy.memorial.ui.tour.guide

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import fr.uha.hassenforder.easy.android.ui.DecoratorProperties
import fr.uha.hassenforder.easy.android.ui.field.text.OutlinedTextField
import fr.uha.hassenforder.easy.android.ui.field.text.TextProperties
import fr.uha.hassenforder.easy.android.ui.field.video.AssetPicker
import fr.uha.hassenforder.easy.android.ui.field.video.AssetPickerProperties
import fr.uha.hassenforder.easy.android.ui.field.video.Eraser
import fr.uha.hassenforder.easy.android.ui.field.video.GalleryPicker
import fr.uha.hassenforder.easy.android.ui.field.video.GalleryPickerProperties
import fr.uha.hassenforder.easy.android.ui.field.video.OutlinedVideoField
import fr.uha.hassenforder.easy.android.ui.field.video.VideoRecorder
import fr.uha.hassenforder.easy.android.ui.field.video.VideoRecorderProperties
import fr.uha.hassenforder.easy.memorial.provider.MemorialDocumentProvider
import fr.uha.hassenforder.easy.memorial.R

@Composable
fun GuideDescriptionScreen(
    uiState: GuideViewModel.UIState,
    send : (GuideViewModel.UIEvent) -> Unit,
) {
    LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = uiState.guide.poi.name,
            onValueChange = { send(GuideViewModel.UIEvent.TitleChanged(it)) },
            properties = TextProperties(),
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.title,
                errorId = uiState.validation.title
            )
        )
        OutlinedTextField(
            value = uiState.guide.poi.description,
            onValueChange = { send(GuideViewModel.UIEvent.DescriptionChanged(it)) },
            properties = TextProperties(),
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.description,
                errorId = uiState.validation.description
            ),
        )
        OutlinedVideoField(
            value = uiState.guide.poi.media,
            onValueChange = { send(GuideViewModel.UIEvent.MediaChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.media,
                errorId = uiState.validation.media
            ),
            top = @Composable {
                Eraser()
            },
            middle = @Composable {
                GalleryPicker(
                    properties = GalleryPickerProperties(
                        galleryFilter = "video/*",
                    ),
                )
                LocalContext.current
                AssetPicker(
                    properties = AssetPickerProperties(
                        uriProvider = { id -> MemorialDocumentProvider.getAssetDocumentUri(id) },
                    ),
                )
            },
            bottom = @Composable {
                LocalContext.current
                VideoRecorder(
                    properties = VideoRecorderProperties(
                        filenameProvider = { MemorialDocumentProvider.createExternalVideoFilename() },
                    ),
                )
            }
        )
    }
}