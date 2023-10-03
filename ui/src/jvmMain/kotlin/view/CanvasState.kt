package view

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import org.jetbrains.skia.Bitmap


enum class DrawMode {
    PAINT {
        override fun toggle() = ERASE
    }, ERASE {
        override fun toggle() = PAINT
    };

    abstract fun toggle(): DrawMode

}

class CanvasState {

    /** The bitmap which all drawing commands will be applied to.
    This is where we will get the raw pixels of the image for inference
     **/
    private val bitmap = ImageBitmap(512, 512, hasAlpha = false)


    var paths = mutableStateListOf<Pair<Path, StrokeProperties>>()
    var undoneOperations = mutableStateListOf<Pair<Path, StrokeProperties>>()

    var currentPath: Path = Path()

    var previousPosition by mutableStateOf(Offset.Unspecified)
    var currentPosition by mutableStateOf(Offset.Unspecified)
    var motionEvent by mutableStateOf(MotionEvent.IDLE)

    var drawMode by mutableStateOf(DrawMode.PAINT)
    var strokeSize by mutableStateOf(6.0f)


    fun undoOperation() {
        if (paths.isEmpty()) return
        val lastPath = paths.removeLast()
        undoneOperations.add(lastPath)
    }

    fun redoOperation() {
        if (undoneOperations.isEmpty()) return
        val undoneOp = undoneOperations.removeLast()
        paths.add(undoneOp)
    }

    fun clearUndoneOperations() {
        undoneOperations.clear()
    }

    fun getBitmap(): Bitmap {
        val canvas = Canvas(bitmap).apply { clear() }

        paths.forEach{
            canvas.drawPath(it.first, it.second)
        }

        return bitmap.asSkiaBitmap()
    }

    fun clearCanvas() {
        paths.clear()
        clearUndoneOperations()
//        paths.add(
//            Path()
//            to
//            StrokeProperties()
//        )
    }

}