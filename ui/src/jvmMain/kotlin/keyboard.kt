import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


enum class KeyEvents {
    UNDO, REDO, ERASE_MODE, PAINT_MODE,
    INC_STROKE, DEC_STROKE, CLEAR_CANVAS
}


val keyboard = MutableSharedFlow<KeyEvents>()

private fun emitKeyboardEvent(event: KeyEvents) {
    CoroutineScope(Dispatchers.Main).launch {
        keyboard.emit(event)
    }
}


val keySet = mutableSetOf<Key>()
private fun checkKeyPressedAndFireEvent(key: Key, event: KeyEvents) {
    if (keySet.contains(key)) return
    else {
        keySet.add(key)
        emitKeyboardEvent(event)
    }
}

private fun onKeyUp(key: Key) {
    keySet.remove(key)
}

@OptIn(ExperimentalComposeUiApi::class)
val onKeyEvent = lambda@ { event: KeyEvent ->

    if (event.type == KeyEventType.KeyUp) {
        onKeyUp(event.key)
        return@lambda true
    }
    if (event.isCtrlPressed and (event.type == KeyEventType.KeyDown)) {
        when (event.key) {
            Key.Z -> checkKeyPressedAndFireEvent(Key.Z, KeyEvents.UNDO)
            Key.Y -> checkKeyPressedAndFireEvent(Key.Y, KeyEvents.REDO)
            else -> Unit
        }
    } else {
        when(event.key) {
            Key.E -> checkKeyPressedAndFireEvent(Key.E, KeyEvents.ERASE_MODE)
            Key.P -> checkKeyPressedAndFireEvent(Key.P, KeyEvents.PAINT_MODE)
            Key.X -> checkKeyPressedAndFireEvent(Key.X, KeyEvents.CLEAR_CANVAS)
            Key.LeftBracket -> checkKeyPressedAndFireEvent(Key.LeftBracket, KeyEvents.DEC_STROKE)
            Key.RightBracket -> checkKeyPressedAndFireEvent(Key.RightBracket ,KeyEvents.INC_STROKE)
        }
    }
    true
}