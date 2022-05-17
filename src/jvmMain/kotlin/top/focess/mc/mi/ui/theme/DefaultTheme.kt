package top.focess.mc.mi.ui.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color

object DefaultTheme {
    val outputBoarder = Color.Black
    val inputBoarder = Color.White
    val default: Colors = darkColors(
        primary = Color.Green
    )
    val simulationCell = Color(0xFF9A9A9A)
    val simulationCellBoarder = Color.White
    val simulation = Color(0xFF000000)
}