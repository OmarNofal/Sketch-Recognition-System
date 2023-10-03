package view

import androidx.compose.ui.graphics.*


fun Canvas.clear() {
    drawRect(
        0.0f, 0.0f, 512.0f, 512.0f, Paint().apply{color = Color.White}
    )
}


fun Canvas.drawPath(
    path: Path,
    strokeProperties: StrokeProperties
) {
    drawPath(
        path,
        Paint().apply {
            color = strokeProperties.color
            strokeWidth = strokeProperties.width
            style = PaintingStyle.Stroke
            strokeCap = StrokeCap.Round
            strokeJoin = StrokeJoin.Round
        }
    )
}