package fr.uha.hassenforder.easy.memorial.ui.performing.perform

import android.Manifest
import android.util.Range
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import fr.uha.hassenforder.easy.android.ui.field.video.VideoPlayer
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.memorial.dto.GuidePerforming
import fr.uha.hassenforder.easy.memorial.icons.outlined.Candle
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PerformingProgressScreen(
    uiState: PerformingViewModel.UIState,
    send : (PerformingViewModel.UIEvent) -> Unit,
) {
    val context = LocalContext.current
    val guideFound = remember { mutableStateOf<GuidePerforming?>(null) }
    val imageLabels = remember { mutableStateListOf<ImageLabel>() }
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row (
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Mode",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringArrayResource(id = R.array.tourMode)[uiState.progress.mode.ordinal],
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1F)
                )
                Text(
                    text = "State",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringArrayResource(id = R.array.tourState)[uiState.progress.state.ordinal],
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1F)
                )
            }
            if (guideFound.value != null) {
                val found: GuidePerforming = guideFound.value!!
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            guideFound.value = null
                        }
                ) {
                    Text(
                        text = found.guide.poi.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = found.guide.poi.description,
                    )
                    AsyncImage(
                        model = found.guide.poi.picture,
                        modifier = Modifier.width(192.dp),
                        contentDescription = null,
                        error = rememberVectorPainter(Icons.Outlined.Candle),
                        placeholder = rememberVectorPainter(Icons.Outlined.HourglassBottom),
                    )
                    VideoPlayer(
                        uri = found.guide.poi.media,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                Column {
                    Text(
                        text = imageLabels.joinToString(limit = 3) { it.text },
                    )
                    CameraView(
                        analyzer = ImageLabelingAnalyzer {
                            imageLabels.clear()
                            imageLabels.addAll(it)
                            if (guideFound.value == null) {
                                guideFound.value = findTrigger(uiState.guides, it)
                                if (guideFound.value != null) {
                                    send (PerformingViewModel.UIEvent.GuideDoneState(guideFound.value!!))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    } else if (cameraPermissionState.status.shouldShowRationale) {
        Toast.makeText(context, stringResource(fr.uha.hassenforder.easy.android.R.string.camera_rationale), Toast.LENGTH_LONG).show()
    } else {
        LaunchedEffect(Unit) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
}

private fun findTrigger(
    guides: List<GuidePerforming>,
    imageLabels: List<ImageLabel>
): GuidePerforming? {
    val found = mutableListOf<GuidePerforming>()
    imageLabels.forEach { label ->
        val lowerCaseLabel = label.text.lowercase()
        guides.forEach { guide ->
            if (guide.guide.trigger.label == lowerCaseLabel) {
                found.add(guide)
            }
        }
    }
    if (found.isEmpty()) return null
    return found.first()
}

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

@Composable
private fun CameraView(
    analyzer: ImageAnalysis.Analyzer,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var preview by remember { mutableStateOf<Preview?>(null) }
    val executor = ContextCompat.getMainExecutor(context)
    val cameraProvider = cameraProviderFuture.get()
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        modifier = modifier,
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
