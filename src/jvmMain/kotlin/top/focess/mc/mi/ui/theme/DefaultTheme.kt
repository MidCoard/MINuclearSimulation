package top.focess.mc.mi.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object DefaultTheme {

    val simulationCell = Color(0xFF9A9A9A)
    val simulationCellBoarder = Color.White
    val simulationSelectedCellBoarder = Color.Black

    @Composable
    fun textFieldDefault() = TextFieldDefaults.outlinedTextFieldColors(
            unfocusedBorderColor = Color.Black
        )

    @Composable
    fun checkboxDefault() = CheckboxDefaults.colors(
        uncheckedColor = Color.Black
    )

    @Composable
    fun defaultTextStyle() = TextStyle(
        fontSize = 20.sp,
        fontStyle = FontStyle.Normal,
        color = MaterialTheme.colors.onBackground
    )

    @Composable
    fun defaultPadding() = Modifier.padding(10.dp,5.dp)

    @Composable
    fun smallTextStyle() = TextStyle(
        fontSize = 14.sp,
        fontStyle = FontStyle.Normal,
        color = MaterialTheme.colors.onBackground
    )

    @Composable
    fun defaultBorder() = Modifier.border(1.dp, MaterialTheme.colors.background)

    @Composable
    fun outputBorder() = defaultBorder()

    @Composable
    fun inputBorder() = Modifier.border(1.dp, MaterialTheme.colors.primary)

    @Composable
    fun smallerTextStyle() = TextStyle(
        fontSize = 10.sp,
        fontStyle = FontStyle.Normal,
        color = MaterialTheme.colors.onBackground
    )

    private val lightBlue = Color(0xFFD4EBF2)
    private val deepLightBlue = Color(0xFF99D0E0)
    private val textColor = Color(0xFF1E1E1E);

    private val darkBlue = Color(0xFF17414D)
    private val deepDarkBlue = Color(0xFF3CA6C5)

    val default = lightColors(
        background = lightBlue,
        primary = deepLightBlue,
        onBackground = textColor,
    )
    val dark = darkColors(
        background = darkBlue,
        primary = deepDarkBlue,
    )
}