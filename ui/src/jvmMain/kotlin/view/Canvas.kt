package view

import KeyEvents
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import gestures.drawingGestures
import keyboard
import kotlinx.coroutines.flow.collect
import org.jetbrains.skia.Bitmap


enum class MotionEvent {
    DOWN, MOVE, UP, IDLE
}


@Composable
fun PainterCanvas(
    modifier: Modifier,
    onDrawingChanged: (Bitmap) -> Unit,
    canvasState: CanvasState
) {

    with(canvasState) {
        val brushColor = if (drawMode == DrawMode.PAINT) Color.Black else Color.White

        val currentPathProperties = StrokeProperties(
            brushColor, strokeSize
        )


        KeyboardEventHandler(
            onUndoPressed = { undoOperation(); onDrawingChanged(getBitmap()) },
            onRedoPressed = { redoOperation(); onDrawingChanged(getBitmap()) },
            onEraseMode = { drawMode = DrawMode.ERASE },
            onPaintMode = { drawMode = DrawMode.PAINT },
            onIncreaseStrokeSize = { strokeSize += 2.0f; strokeSize = strokeSize.coerceIn(4.0f, 10.0f) },
            onDecreaseStrokeSize = { strokeSize -= 2.0f; strokeSize = strokeSize.coerceIn(4.0f, 10.0f) },
            onClearCanvas = { clearCanvas(); onDrawingChanged(getBitmap()) }
        )
        val m = modifier.drawingGestures(
            onDragStart = { pointerInputChange ->
                // Create the path that we will populate
                currentPosition = pointerInputChange.position
                motionEvent = MotionEvent.DOWN
                pointerInputChange.consumeDownChange()
            },
            onDragMove = { pointerInputChange ->
                motionEvent = MotionEvent.MOVE
                currentPosition = pointerInputChange.position
                pointerInputChange.consumePositionChange()
            },
            onDragEnd = { pointerInputChange ->
                motionEvent = MotionEvent.UP
                pointerInputChange.consumeDownChange()
            }
        )

        // The actual canvas shown to the user that receives user interaction
        androidx.compose.foundation.Canvas(m.background(Color.White)) {


            when (motionEvent) {

                MotionEvent.DOWN -> {
                    currentPath.moveTo(currentPosition.x, currentPosition.y)
                    previousPosition = currentPosition

                }

                MotionEvent.MOVE -> {
                    currentPath.quadraticBezierTo(
                        previousPosition.x,
                        previousPosition.y,
                        (previousPosition.x + currentPosition.x) / 2,
                        (previousPosition.y + currentPosition.y) / 2

                    )
                    previousPosition = currentPosition
                }

                MotionEvent.UP -> {
                    paths.add(Pair(currentPath, currentPathProperties))
                    clearUndoneOperations()


                    currentPath = Path().apply { moveTo(previousPosition.x, previousPosition.y) }

                    previousPosition = currentPosition
                    currentPosition = Offset.Unspecified

                    motionEvent = MotionEvent.IDLE

                    onDrawingChanged(getBitmap())
                }

                else -> Unit
            }

            paths.forEach { (path, pathProperties) ->
                drawPath(
                    path,
                    brush = SolidColor(pathProperties.color),
                    style = Stroke(width = pathProperties.width, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
            drawPath(
                currentPath,
                brush = SolidColor(currentPathProperties.color),
                style = Stroke(width = currentPathProperties.width, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }

}





@Composable
private fun KeyboardEventHandler(
    onUndoPressed: () -> Unit,
    onRedoPressed: () -> Unit,
    onEraseMode: () -> Unit,
    onPaintMode: () -> Unit,
    onIncreaseStrokeSize: () -> Unit,
    onDecreaseStrokeSize: () -> Unit,
    onClearCanvas: () -> Unit
) {
    LaunchedEffect(true) {
        keyboard.collect { event ->
            when(event) {
                KeyEvents.UNDO -> onUndoPressed()
                KeyEvents.REDO -> onRedoPressed()
                KeyEvents.ERASE_MODE -> onEraseMode()
                KeyEvents.PAINT_MODE -> onPaintMode()
                KeyEvents.INC_STROKE -> onIncreaseStrokeSize()
                KeyEvents.DEC_STROKE -> onDecreaseStrokeSize()
                KeyEvents.CLEAR_CANVAS -> onClearCanvas()
            }
        }
    }
}