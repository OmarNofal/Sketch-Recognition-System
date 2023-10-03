package view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.ClassPrediction
import model.Network5ClassPrediction
import model.Network7ClassPrediction
import java.io.File


@Composable
fun PredictionsPanel(
    modifier: Modifier,
    predictions: List<ClassPrediction>
) {

    if (predictions.isEmpty()) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text("Start drawing on the Canvas", fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
        }
        return
    }

    Column(modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {


        when(predictions.first()) {
            is Network7ClassPrediction ->
                Network7Predictions(predictions.filterIsInstance<Network7ClassPrediction>())
            is Network5ClassPrediction ->
                Network5Predictions(predictions.filterIsInstance<Network5ClassPrediction>())
        }

    }


}

@Composable
private fun Network5Predictions(predictions: List<Network5ClassPrediction>) {
    predictions.forEach{
        val name = it.className.replaceFirstChar { c -> c.uppercaseChar() }
        val conf = (it.probability * 100).toInt()

        Text("$name: $conf%", fontSize = 24.sp, fontFamily = FontFamily.Monospace)
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun Network7Predictions(predictions: List<Network7ClassPrediction>) {
    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {

        predictions.forEach {

            Column(verticalArrangement = Arrangement.SpaceEvenly) {
                Text("" +
                        it.className.replaceFirstChar { it.uppercaseChar() },
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(6.dp),
                    fontSize = 24.sp
                )
                Image(
                    modifier = Modifier.weight(1f),
                    bitmap = loadImageBitmap(File(it.bestSketchPath).inputStream()),
                    contentDescription = null
                )
                it.bestPhotosPaths.forEach {
                    Image(
                        modifier = Modifier.weight(1f),
                        bitmap = loadImageBitmap(File(it).inputStream()),
                        contentDescription = null
                    )
                }
            }

        }


    }
}