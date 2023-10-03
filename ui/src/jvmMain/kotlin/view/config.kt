package view

import androidx.compose.ui.platform.DefaultViewConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.Density





class MyViewConfiguration(
    private val density: Density
) : ViewConfiguration by DefaultViewConfiguration(density) {

    override val touchSlop: Float
        get() = with(density) { return 1.toDp().toPx()}
}