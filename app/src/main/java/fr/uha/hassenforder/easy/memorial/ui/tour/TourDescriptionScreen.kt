package fr.uha.hassenforder.easy.memorial.ui.tour

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import fr.uha.hassenforder.easy.android.ui.DecoratorProperties
import fr.uha.hassenforder.easy.android.ui.field.picture.Eraser
import fr.uha.hassenforder.easy.android.ui.field.picture.ExternalCameraApp
import fr.uha.hassenforder.easy.android.ui.field.picture.ExternalCameraAppProperties
import fr.uha.hassenforder.easy.android.ui.field.picture.GalleryPicker
import fr.uha.hassenforder.easy.android.ui.field.picture.OutlinedPictureField
import fr.uha.hassenforder.easy.android.ui.field.text.OutlinedTextField
import fr.uha.hassenforder.easy.android.ui.field.picture.GalleryPickerProperties
import fr.uha.hassenforder.easy.android.ui.field.text.TextProperties
import fr.uha.hassenforder.easy.memorial.provider.MemorialDocumentProvider
import fr.uha.hassenforder.easy.memorial.R

@Composable
fun TourDescriptionScreen(
    uiState: TourViewModel.UIState,
    send : (TourViewModel.UIEvent) -> Unit,
) {
    LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = uiState.tour.tour.title,
            onValueChange = { send(TourViewModel.UIEvent.TitleChanged(it)) },
            properties = TextProperties(),
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.title,
                errorId = uiState.validation.title
            )
        )
        OutlinedPictureField(
            value = uiState.tour.tour.picture,
            onValueChange = { send(TourViewModel.UIEvent.PictureChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties (
                labelId = R.string.picture,
                errorId = uiState.validation.picture
            ),
            top = @Composable {
                Eraser()
            },
            middle = @Composable {
                GalleryPicker(
                    properties = GalleryPickerProperties(
                        galleryFilter = "image/*",
                    ),
                )
            },
            bottom = @Composable {
                val context = LocalContext.current
                ExternalCameraApp(
                    properties = ExternalCameraAppProperties(
                        uriProvider = { MemorialDocumentProvider.createExternalImageUri(context) },
                    ),
                )
            }
        )
        OutlinedTextField(
            value = uiState.tour.tour.description,
            onValueChange = { send(TourViewModel.UIEvent.DescriptionChanged(it)) },
            properties = TextProperties(),
            modifier = Modifier.fillMaxWidth(),
            decorator = DecoratorProperties(
                labelId = R.string.description,
                errorId = uiState.validation.description
            ),
        )
    }
}