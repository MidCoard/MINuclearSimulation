package top.focess.mc.mi.ui.theme

import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Colors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object DefaultTheme {

    @Composable
    fun textFieldDefault() = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = Color.Black
        )

    @Composable
    fun checkboxDefault() =  CheckboxDefaults.colors(
        uncheckedColor = Color.Black
    )

    val simulationSelectedCellBoarder = Color.Black
    val outputBoarder = Color.Black
    val inputBoarder = Color.White
    val default: Colors = darkColors(
        primary = Color.Green,
    )
    val simulationCell = Color(0xFF9A9A9A)
    val simulationCellBoarder = Color.White
}