package top.focess.mc.mi.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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

    @Composable
    fun defaultTextField() = if (isSystemInDarkTheme()) darkTextField() else lightTextField()

    @Composable
    fun lightTextField() = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colors.onBackground,
        unfocusedBorderColor = MaterialTheme.colors.secondaryVariant,
        backgroundColor = MaterialTheme.colors.secondary
    )

    @Composable
    fun darkTextField() = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colors.onBackground,
        unfocusedBorderColor = MaterialTheme.colors.secondaryVariant,
        backgroundColor = MaterialTheme.colors.secondary
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
    fun selectedBorder() = Modifier.border(1.dp, MaterialTheme.colors.primary)

    @Composable
    fun smallerTextStyle() = TextStyle(
        fontSize = 10.sp,
        fontStyle = FontStyle.Normal,
        color = MaterialTheme.colors.onBackground
    )

    @Composable
    fun selectedBorder(selected: Boolean) = if (selected) selectedBorder() else defaultBorder()

    private val lightBlue = Color(0xFFD4EBF2)
    private val littleLightBlue = Color(0xFF7AC1D7)
    private val deepLightBlue = Color(0xFF99D0E0)
    private val textColor = Color(0xFF1E1E1E);

    private val darkBlue = Color(0xFF050E11)
    private val lightDarkBlue = Color(0xFF0A1B20)
    private val deepDarkBlue = Color(0xFF3CA6C5)
    private val lightTextColor = Color(0xFFDDDDDD)

    private val lightGray = Color(0xFF7B7B7B)

    val default = lightColors(
        background = lightBlue,
        primary = deepLightBlue,
        onBackground = textColor,
        secondary = littleLightBlue,
        secondaryVariant = Color.Black.copy(alpha = 0.5f)
    )
    val dark = darkColors(
        background = darkBlue,
        primary = deepDarkBlue,
        onBackground = lightTextColor,
        secondary = lightDarkBlue,
        secondaryVariant = lightGray
    )
}