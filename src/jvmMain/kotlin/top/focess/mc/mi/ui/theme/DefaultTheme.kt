package top.focess.mc.mi.ui.theme

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
    fun defaultTextField() = TextFieldDefaults.outlinedTextFieldColors(
        textColor = MaterialTheme.colors.onBackground,
        unfocusedBorderColor = MaterialTheme.colors.secondaryVariant,
        backgroundColor = MaterialTheme.colors.secondary,
    )

    @Composable
    fun defaultCheckBox() = CheckboxDefaults.colors(
        uncheckedColor = MaterialTheme.colors.secondaryVariant,
        checkedColor = MaterialTheme.colors.secondary,
        checkmarkColor = MaterialTheme.colors.primary
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
    fun squarePadding() = Modifier.padding(10.dp)

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
    fun midSmallTextStyle() = TextStyle(
        fontSize = 12.sp,
        fontStyle = FontStyle.Normal,
        color = MaterialTheme.colors.onBackground
    )

    @Composable
    fun selectedBorder(selected: Boolean) = if (selected) selectedBorder() else defaultBorder()

    @Composable
    fun selectedBorderAndBackground(selected: Boolean) = if(selected) selectedBorder(selected).background(color = MaterialTheme.colors.primary) else selectedBorder(selected).background(color = MaterialTheme.colors.secondary)

    @Composable
    fun defaultScrollbar() = ScrollbarStyle(
        hoverColor = MaterialTheme.colors.secondaryVariant,
        unhoverColor = MaterialTheme.colors.secondaryVariant.copy(0.5f),
        minimalHeight = 16.dp,
        thickness = 8.dp,
        shape = RoundedCornerShape(4.dp),
        hoverDurationMillis = 300,
    )

    private var default:Colors? = null;

    fun setDefaultColor(colors: Colors) {
        default = colors
    }

    private val lightBlue = Color(0xFFD4EBF2)
    private val littleLightBlue = Color(0xFF3FA6C5)
    private val deepLightBlue = Color(0xFF99D0E0)
    private val textColor = Color(0xFF1E1E1E);

    private val darkBlue = Color(0xFF050E11)
    private val lightDarkBlue = Color(0xFF0A1B20)
    private val deepDarkBlue = Color(0xFF3CA6C5)
    private val lightTextColor = Color(0xFFDDDDDD)

    private val lightGray = Color(0xFF7B7B7B)


    val light = lightColors(
        background = lightBlue,
        primary = littleLightBlue,
        onBackground = textColor,
        secondary = deepLightBlue,
        secondaryVariant = Color.Black.copy(alpha = 0.5f),
    )

    val dark = darkColors(
        background = darkBlue,
        primary = deepDarkBlue,
        onBackground = lightTextColor,
        secondary = lightDarkBlue,
        secondaryVariant = lightGray,
    )

    @Composable
    fun isThemeDark() = if (default != null) default == dark else isSystemInDarkTheme()

    @Composable
    fun getDefault() = default ?: if (isSystemInDarkTheme()) dark else light

}