package view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import network.NNetVersion
import network.Network
import org.jetbrains.skia.Bitmap


@Composable
fun SketchUI(
    modifier: Modifier,
    onNetworkChosen: (NNetVersion) -> Unit,
    onDrawingChanged: (Bitmap) -> Unit
) {

    val canvasState = remember { CanvasState() }

    Column(
        modifier = modifier.then(Modifier)
    ) {


        PainterCanvas(
            Modifier
                .padding(8.dp)
                .width(with(LocalDensity.current) { 512.toDp() })
                .height(with(LocalDensity.current) { 512.toDp() })
                .border(2.dp, color = Color.Black)
                .clipToBounds(),
            onDrawingChanged,
            canvasState
        )

        PainterToolbar(
            Modifier.padding(8.dp).width(with(LocalDensity.current) { 512.toDp() }),
            canvasState.strokeSize,
            canvasState.drawMode,
            Network.version,
            { canvasState.strokeSize = it },
            { canvasState.drawMode = DrawMode.PAINT },
            { canvasState.drawMode = DrawMode.ERASE },
            { canvasState.undoOperation(); onDrawingChanged(canvasState.getBitmap()) },
            { canvasState.redoOperation(); onDrawingChanged(canvasState.getBitmap()) },
            canvasState.paths.isNotEmpty(),
            canvasState.undoneOperations.isNotEmpty(),
            onNetworkChosen
        )
    }

}