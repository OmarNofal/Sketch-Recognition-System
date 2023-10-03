import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.*
import model.ClassPrediction
import network.NNetVersion
import network.Network
import org.jetbrains.skia.Bitmap
import org.jetbrains.skiko.toBufferedImage
import view.MyViewConfiguration
import view.PredictionsPanel
import view.SketchUI
import java.awt.Image
import java.awt.image.BufferedImage


/**
 * Global image bitmap which contains the
 * current drawing the user has
 */
lateinit var imageBitmap: Bitmap


private fun preprocessForModel5(): ByteArray {

    val originalBuffer = imageBitmap.toBufferedImage()

    val image = originalBuffer.getScaledInstance(256, 256, Image.SCALE_REPLICATE)
    val newBufferedImage = BufferedImage(256, 256, BufferedImage.TYPE_BYTE_GRAY)
    val graphics = newBufferedImage.createGraphics()
    graphics.drawImage(image, 0, 0, 256, 256) { _, _, _, _, _, _ -> true }
    graphics.dispose()

    val pixelsInt = IntArray(256 * 256)
    newBufferedImage.data.getPixels(0, 0, 256, 256, pixelsInt)

    return ByteArray(256 * 256) { index -> pixelsInt[index].toByte() }
}


private fun preprocessForModel7(): ByteArray {
    val originalBuffer = imageBitmap.toBufferedImage()

    val image = originalBuffer.getScaledInstance(224, 224, Image.SCALE_REPLICATE)
    val newBufferedImage = BufferedImage(224, 224, BufferedImage.TYPE_3BYTE_BGR)
    val graphics = newBufferedImage.createGraphics()
    graphics.drawImage(image, 0, 0, 224, 224) { _, _, _, _, _, _ -> true }
    graphics.dispose()


    val rgba = IntArray(224 * 224 * 3)
    newBufferedImage.data.getPixels(0, 0, 224, 224, rgba)

    val networkData = ByteArray(224 * 224 * 3)
    // Flip pixels to RGB
    for (i in rgba.indices step 3) {
        networkData[i] = rgba[i + 2].toByte()
        networkData[i + 1] = rgba[i + 1].toByte()
        networkData[i + 2] = rgba[i].toByte()
    }

    return networkData
}


var predictions by mutableStateOf(listOf<ClassPrediction>())

private val coroutineScope = CoroutineScope(Dispatchers.IO)
private var currentNetworkJob: Job? = null
fun onDrawingChanged() {
    if (!::imageBitmap.isInitialized) return
    currentNetworkJob?.cancel()
    currentNetworkJob = coroutineScope.launch {
        try {
            val data = if (Network.version == NNetVersion.FIVE) preprocessForModel5() else preprocessForModel7()
            if (data.all { it == (255).toByte() }) {
                predictions = listOf()
                return@launch
            }
            val results = Network.predictTop5(data)
            if (!isActive) return@launch
            else
                predictions = results
        } catch(e: JsonSyntaxException) {
            if (!isActive) return@launch
            println("Malformed message from the server")
        }
    }
}



@Composable
@Preview
fun App() {


    MaterialTheme {

        Row(modifier = Modifier.fillMaxWidth().fillMaxHeight()) {


            Box(modifier = Modifier.weight(0.5f).fillMaxWidth().fillMaxHeight(), contentAlignment = Alignment.Center) {
                SketchUI(
                    modifier = Modifier,
                    onNetworkChosen = { Network.version = it; onDrawingChanged() }
                ) {
                    imageBitmap = it
                    onDrawingChanged()
               }
            }

            Divider(
                modifier = Modifier.fillMaxHeight().weight(0.02f),
            )

            PredictionsPanel(
                modifier = Modifier.weight(1f).fillMaxHeight().fillMaxWidth(),
                predictions = predictions
            )
        }
    }
}




fun main() = application {
    Window(
        title = "Sketch App",
        onCloseRequest = ::exitApplication,
        onKeyEvent = onKeyEvent,
        state = WindowState(
            placement = WindowPlacement.Maximized
        )
    )
    {
        val myViewConfiguration = MyViewConfiguration(LocalDensity.current)
        CompositionLocalProvider(
            LocalViewConfiguration provides myViewConfiguration
        ) {
            App()
        }


    }
}
