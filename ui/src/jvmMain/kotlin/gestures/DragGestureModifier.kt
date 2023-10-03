package gestures

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput


suspend fun PointerInputScope.detectDrawing(
    onDragStart: (PointerInputChange) -> Unit,
    onDragMove: (PointerInputChange) -> Unit,
    onDragEnd: (PointerInputChange) -> Unit
) {

    awaitPointerEventScope {

        // detect that the user touched the canvas
        val downEvent = awaitFirstDown()
        onDragStart(downEvent)

        // we need to detect if he entered drag mode or not
        val change: PointerInputChange? = awaitTouchSlopOrCancellation(downEvent.id) { change: PointerInputChange, _: Offset ->
            // we need to tell compose that we intend to consume the movement
            // otherwise it would return null
            change.consumePositionChange()
        }

        if (change != null) {
            // keep polling for drag movements
            drag(change.id, onDragMove)

            // no more pointers pressed
            onDragEnd(downEvent)
        }

    }

}



fun Modifier.drawingGestures(
    onDragStart: (PointerInputChange) -> Unit,
    onDragMove: (PointerInputChange) -> Unit,
    onDragEnd: (PointerInputChange) -> Unit
) = this.then(
    Modifier
        .pointerInput(Unit) {

            forEachGesture {
                detectDrawing(onDragStart, onDragMove, onDragEnd)
            }
        }
)