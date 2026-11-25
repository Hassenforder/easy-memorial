package fr.uha.hassenforder.easy.memorial.ui.field.picture

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Range
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MimeTypes
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import fr.uha.hassenforder.easy.android.ui.dialog.OutlinedDialog
import fr.uha.hassenforder.easy.android.ui.field.GenericActionControl
import fr.uha.hassenforder.easy.android.ui.field.picture.DecoratedButton
import fr.uha.hassenforder.easy.android.ui.field.picture.PictureFieldActionsScope
import fr.uha.hassenforder.easy.android.ui.field.text.TextFieldActionsScope
import fr.uha.hassenforder.easy.memorial.R
import java.util.concurrent.Executors

data class IACameraProperties (
    val filenameProvider : (() -> String),
    val icon : ImageVector = Icons.Outlined.Camera,
    @param:StringRes val titleId : Int = R.string.title_ia_photo,
    @param:StringRes val badgeId : Int = R.string.picker_short_ia,
)

private class ImageLabelingAnalyzer(
    private val onImageLabeled: (labels: List<ImageLabel>) -> Unit
) : ImageAnalysis.Analyzer {

    private val options = ImageLabelerOptions.Builder()
        .setConfidenceThreshold(0.7f)
        .build()

    private val labeler = ImageLabeling.getClient(options)

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.image?.let {
            val image = InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)
            labeler.process(image)
                .addOnSuccessListener { labels ->
                    onImageLabeled(labels)
                    imageProxy.close()
                }
        }
    }
}

private fun cameraCapture (
    context : Context,
    imageCapture : ImageCapture,
    onSelected: (Uri?) -> Unit,
    properties: IACameraProperties,
) {
    val filename = properties.filenameProvider()
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, MimeTypes.IMAGE_JPEG)
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+"/"+"memorial")
    }
    val outputFileOptions = ImageCapture.OutputFileOptions
        .Builder(context.contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        .build()
    val executor = ContextCompat.getMainExecutor(context)
    imageCapture.takePicture(outputFileOptions, executor, object: ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            onSelected(outputFileResults.savedUri)
        }
        override fun onError(exception: ImageCaptureException) {
            onSelected(null)
        }
    })
}

@Composable
private fun CameraView(
    analyzer: ImageAnalysis.Analyzer,
    onSelected: (Uri?) -> Unit,
    properties: IACameraProperties,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var preview by remember { mutableStateOf<Preview?>(null) }
    val executor = ContextCompat.getMainExecutor(context)
    val cameraProvider = cameraProviderFuture.get()
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    AndroidView(
        modifier = modifier
            .clickable {
                cameraCapture(context, imageCapture, onSelected, properties)
            },
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FIT_CENTER
            }
            cameraProviderFuture.addListener({
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .apply {
                        setAnalyzer(cameraExecutor, analyzer)
                    }
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    imageAnalysis,
                    imageCapture,
                    preview
                )
            }, executor)
            preview = Preview.Builder()
                .setTargetFrameRate(Range(2,2))
                .build()
                .also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
            previewView
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ImageLabelingDialog(
    onSelected: (Uri?) -> Unit,
    properties: IACameraProperties,
) {
    val context = LocalContext.current
    val imageLabels = remember { mutableStateListOf<ImageLabel>() }
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted) {
        OutlinedDialog(onDismiss = { onSelected(null) }, titleId = properties.titleId) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = imageLabels.joinToString(separator = "\n", limit=5) { it.text },
                minLines = 5,
                maxLines = 5,
            )
            CameraView(
                analyzer = ImageLabelingAnalyzer {
                    imageLabels.clear()
                    imageLabels.addAll(it)
                },
                onSelected = onSelected,
                properties = properties,
                modifier = Modifier.fillMaxWidth()
            )
        }
    } else if (cameraPermissionState.status.shouldShowRationale) {
        Toast.makeText(context, stringResource(fr.uha.hassenforder.easy.android.R.string.camera_rationale), Toast.LENGTH_LONG).show()
    } else {
        LaunchedEffect(Unit) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
}

@Composable
fun IACameraControl (
    onValueChange: (Uri?) -> Unit,
    properties: IACameraProperties,
) {
    val showPopup = remember { mutableStateOf(false) }
    if (showPopup.value) {
        ImageLabelingDialog(
            onSelected = { uri ->
                showPopup.value = false
                uri?.let { onValueChange(uri) }
            },
            properties,
        )
    }
    DecoratedButton(
        { showPopup.value = true },
        properties.icon,
        properties.badgeId,
        "take labeled picture",
    )
}

@Composable
fun PictureFieldActionsScope.IACamera(properties: IACameraProperties) = IACameraControl(onValueChange, properties)

@Composable
private fun ActionIALabellerControl (
    onClick: () -> Unit,
    enabled : Boolean,
){
    GenericActionControl(
        onClick = onClick,
        icon = Icons.AutoMirrored.Filled.Label,
        enabled = enabled
    )
}

@Composable
fun TextFieldActionsScope.ActionIALabeller(
    onClick: () -> Unit,
    enabled : Boolean,
) = ActionIALabellerControl(onClick, enabled)
