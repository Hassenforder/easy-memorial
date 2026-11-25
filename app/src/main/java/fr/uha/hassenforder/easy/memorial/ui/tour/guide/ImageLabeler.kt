package fr.uha.hassenforder.easy.memorial.ui.tour.guide

import android.content.Context
import android.content.res.AssetManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import fr.uha.hassenforder.easy.android.ui.app.AppTitle
import fr.uha.hassenforder.easy.android.ui.dialog.OutlinedDialog
import fr.uha.hassenforder.easy.memorial.R
import fr.uha.hassenforder.easy.memorial.icons.outlined.Candle
import java.io.IOException
import java.util.concurrent.Executors


private class ImageLabelingAnalyzer(
    private val onImageLabeled: (labels: List<ImageLabel>) -> Unit
) {
    private fun imageProvider (context : Context, uri : Uri) : InputImage? {
        val primaryFolder = uri.pathSegments[0]
        if (primaryFolder == "android_asset" && uri.path != null) {
            val assetManager: AssetManager = context.assets
            try {
                val filePath = uri.path!!.substring(primaryFolder.length+2)
                val inputStream = assetManager.open(filePath)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                return InputImage.fromBitmap(bitmap, 0)
            } catch (exception : IOException) {
                return null
            }
        } else {
            return InputImage.fromFilePath(context, uri)
        }
    }

    fun analyze(context : Context, uri : Uri) {
        try {
            val options = ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build()
            val labeler = ImageLabeling.getClient(options)
            val image = imageProvider(context, uri)
            if (image != null) {
                labeler
                    .process(image)
                    .addOnSuccessListener { labels ->
                        onImageLabeled(labels)
                    }
                    .addOnFailureListener { e ->
                        onImageLabeled(listOf(ImageLabel(e.message, 0.0F, -1)))
                    }
            } else {
                onImageLabeled(listOf(ImageLabel("cannot open the file", 0.0F, -1)))
            }
        } catch (e: IOException) {
            onImageLabeled(listOf(ImageLabel(e.message, 0.0F, -1)))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageLabelerDialog(
    value: Uri,
    onSelected: (String?) -> Unit,
) {
    val context = LocalContext.current
    val imageLabels = remember { mutableStateListOf<ImageLabel>() }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    LaunchedEffect(key1 = Unit) {
        cameraExecutor.execute {
            ImageLabelingAnalyzer {
                imageLabels.clear()
                imageLabels.addAll(it)
            }.analyze(context, value)
        }
    }
    OutlinedDialog(onDismiss = { onSelected(null) }, titleId = R.string.title_guide) {
        LazyColumn (
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                AsyncImage(
                    model = value,
                    modifier = Modifier
                        .size(192.dp)
                        .padding(end = 4.dp)
                    ,
                    contentDescription = "Selected image",
                    error = rememberVectorPainter(Icons.Outlined.Candle),
                    placeholder = rememberVectorPainter(Icons.Outlined.HourglassBottom),
                    contentScale = ContentScale.Fit
                )
            }
            items(
                items = imageLabels,
                key = { item -> item.index }
            ) { item ->
                Text(
                    text = item.text,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (item.index == -1) onSelected(null)
                            else onSelected(item.text)
                        }
                )
            }
        }
    }

}
