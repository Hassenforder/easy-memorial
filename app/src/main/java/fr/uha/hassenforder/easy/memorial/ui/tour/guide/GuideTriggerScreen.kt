package fr.uha.hassenforder.easy.memorial.ui.tour.guide

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import fr.uha.hassenforder.easy.android.ui.DecoratorProperties
import fr.uha.hassenforder.easy.android.ui.field.picture.AssetPicker
import fr.uha.hassenforder.easy.android.ui.field.text.OutlinedTextField
import fr.uha.hassenforder.easy.android.ui.field.picture.AssetPickerProperties
import fr.uha.hassenforder.easy.android.ui.field.picture.Eraser
import fr.uha.hassenforder.easy.android.ui.field.picture.GalleryPicker
import fr.uha.hassenforder.easy.android.ui.field.picture.GalleryPickerProperties
import fr.uha.hassenforder.easy.android.ui.field.picture.OutlinedPictureField
import fr.uha.hassenforder.easy.android.ui.field.text.TextProperties
import fr.uha.hassenforder.easy.memorial.provider.MemorialDocumentProvider
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.memorial.ui.field.picture.ActionIALabeller
import fr.uha.hassenforder.easy.memorial.ui.field.picture.IACamera
import fr.uha.hassenforder.easy.memorial.ui.field.picture.IACameraProperties

@Composable
fun GuideTriggerScreen(
    uiState: GuideViewModel.UIState,
    send : (GuideViewModel.UIEvent) -> Unit,
) {
    LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val showPopup = remember { mutableStateOf(false) }
        if (showPopup.value) {
            if (uiState.guide.poi.picture != null) {
                ImageLabelerDialog(
                    uiState.guide.poi.picture,
                    onSelected = { label ->
                        showPopup.value = false
                        if (label != null) send(GuideViewModel.UIEvent.LabelChanged(label))
                    },
                )
            }
        }
        OutlinedPictureField(
            value = uiState.guide.poi.picture,
            onValueChange = { send(GuideViewModel.UIEvent.PictureChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.picture,
                errorId = uiState.validation.picture,
                hintId = R.string.hint_trigger
            ),
            top = @Composable {
                Eraser()
            },
            middle = @Composable {
                AssetPicker (
                    properties = AssetPickerProperties(
                        uriProvider = { id -> MemorialDocumentProvider.getAssetDocumentUri(id) },
                    ),
                )
                GalleryPicker(
                    properties = GalleryPickerProperties(
                        galleryFilter = "image/*",
                    ),
                )
            },
            bottom = @Composable {
                LocalContext.current
                IACamera(
                    properties = IACameraProperties(
                        filenameProvider = { MemorialDocumentProvider.createExternalImageFilename() },
                    ),
                )
            }
        )
        OutlinedTextField(
            value = uiState.guide.trigger.label,
            onValueChange = { send(GuideViewModel.UIEvent.LabelChanged(it)) },
            properties = TextProperties(),
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties (
                labelId = R.string.label,
                errorId = uiState.validation.label,
                hintId = R.string.hint_label,
            ),
            trailing = {
                ActionIALabeller(onClick = { showPopup.value = true }, enabled = uiState.guide.poi.picture != null)
            }
        )
        OutlinedTextField(
            value = uiState.guide.trigger.latitude,
            onValueChange = { send(GuideViewModel.UIEvent.LatitudeChanged(it)) },
            properties = TextProperties(),
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties (
                labelId = R.string.latitude,
                errorId = uiState.validation.latitude
            )
        )
        OutlinedTextField(
            value = uiState.guide.trigger.longitude,
            onValueChange = { send(GuideViewModel.UIEvent.LongitudeChanged(it)) },
            properties = TextProperties(),
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties (
                labelId = R.string.longitude,
                errorId = uiState.validation.longitude
            ),
        )
        OutlinedTextField(
            value = uiState.guide.trigger.hint,
            onValueChange = { send(GuideViewModel.UIEvent.HintChanged(it)) },
            properties = TextProperties(),
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties (
                labelId = R.string.hint,
                errorId = uiState.validation.hint
            ),
        )
    }
}
