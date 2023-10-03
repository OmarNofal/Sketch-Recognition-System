package view


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import network.NNetVersion
import org.jetbrains.skia.Font
import org.jetbrains.skia.Paint
import org.jetbrains.skia.TextLine
import org.jetbrains.skia.Typeface
import java.awt.Cursor


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PainterToolbar(
    modifier: Modifier,
    strokeSize: Float,
    drawMode: DrawMode,
    chosenNNetVersion: NNetVersion,
    onStrokeSizeChange: (Float) -> Unit,
    onDrawModeSelected: () -> Unit,
    onEraseModeSelected: () -> Unit,
    onUndo:() -> Unit,
    onRedo:() -> Unit,
    undoAvailable: Boolean,
    redoAvailable: Boolean,
    onNetworkChosen: (NNetVersion) -> Unit
) {

    var sliderVisible by remember { mutableStateOf(false) }



    Column(modifier) {

        var value by remember { mutableStateOf(0.0f) }

        AnimatedVisibility(sliderVisible) {

            val sliderHoverInteractionSource = remember {  MutableInteractionSource() }
            val isHovered by sliderHoverInteractionSource.collectIsHoveredAsState()

            LaunchedEffect(sliderVisible, isHovered) {
                if (sliderVisible) {
                    delay(800)
                    if (isActive and !isHovered)
                        sliderVisible = false
                }
            }

            val sliderInteractionSource = remember { MutableInteractionSource() }
            val isFocused by sliderInteractionSource.collectIsDraggedAsState()

            var mousePosition by remember { mutableStateOf(Offset.Zero) }
            Slider(
                strokeSize,
                valueRange = 4.0f..8.0f,
                onValueChange = onStrokeSizeChange,
                modifier = Modifier.pointerHoverIcon(PointerIcon(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)))
                    .pointerMoveFilter(
                        onMove = {
                            mousePosition = it
                            false
                                 },
                    )
                    .hoverable(sliderHoverInteractionSource, true)
                    .drawBehind {
                        if (!isHovered) return@drawBehind
                        translate(mousePosition.x - 20.0f, mousePosition.y - 50.0f) {
                            drawRoundRect(
                                Color(0xFFDDDDDD),
                                size = Size(60.0f, 30.0f),
                                topLeft = Offset(-10.0f, -15.0f),
                                cornerRadius = CornerRadius(5.0f)
                            )
                            drawContext.canvas.nativeCanvas.drawTextLine(
                                TextLine.Companion.make(strokeSize.toInt().toString(), Font(typeface = null, size = 18.0f)),
                                10.0f, 5.0f, Paint().apply { }
                            )
                        }
                    },
                interactionSource = sliderInteractionSource
            )

        }

        Surface(
            modifier = Modifier.fillMaxWidth().clip(CircleShape),
            color = Color(222, 222, 222),
        ) {


            Row(
                horizontalArrangement = Arrangement.SpaceAround
            ) {



                IconButton(
                    onClick = onDrawModeSelected,
                    modifier =
                        if (drawMode == DrawMode.PAINT)
                            Modifier.background(Color(0x110000000), shape = CircleShape)
                        else Modifier
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Draw,
                        contentDescription = "Paint Mode"
                    )
                }

                IconButton(
                    onClick = onEraseModeSelected,
                    modifier =
                        if (drawMode == DrawMode.ERASE)
                            Modifier.background(Color(0x110000000), shape = CircleShape)
                        else Modifier,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.EditOff,
                        contentDescription = "Paint Mode"
                    )
                }

                IconButton(
                    onClick = { sliderVisible = !sliderVisible },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FormatPaint,
                        contentDescription = "Paint Mode",
                    )
                }

                IconButton(
                    onClick = onUndo,
                    enabled = undoAvailable
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Undo,
                        contentDescription = "Paint Mode"
                    )
                }

                IconButton(
                    onClick = onRedo,
                    enabled = redoAvailable
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Redo,
                        contentDescription = "Paint Mode"
                    )
                }


            }


        }

        var dropDown by remember { mutableStateOf(false) }

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Network Version", fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Column {
                Row(
                    modifier = Modifier.width(150.dp).border(1.dp, Color.DarkGray, shape = RoundedCornerShape(6.dp))
                        .clickable { dropDown = !dropDown }
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = chosenNNetVersion.value, modifier = Modifier.padding(2.dp))

                    Icon(Icons.Rounded.ArrowDropDown, "Choose Network")
                }

                DropdownMenu(dropDown, { dropDown = false }) {
                    DropdownMenuItem(onClick = {
                        onNetworkChosen(NNetVersion.FIVE); dropDown = false
                    }) { Text("Version 5") }
                    DropdownMenuItem(onClick = {
                        onNetworkChosen(NNetVersion.SEVEN); dropDown = false
                    }) { Text("Version 7") }
                }

            }
        }





    }

}