package top.focess.mc.mi.ui.textfield

import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun  LongTextField(value: Long, modifier: Modifier, colors: TextFieldColors , enabled: Boolean = true,label: @Composable ()->Unit, onValueChange: (Long) -> Unit) {
    var text by remember {mutableStateOf(value.toString(), neverEqualPolicy()) }
    var previousText by remember { mutableStateOf(text) }
    if ((previousText.isEmpty() || previousText == "-") && value == 0L && text != previousText)
        text = previousText
    else if (value != 0L && value.toString() != text)
        text = value.toString()
    else if (value == 0L && text != "0" && text != "-" && text.isNotEmpty())
        text = "0"
    OutlinedTextField(value = text , modifier = modifier, label = label, enabled = enabled, onValueChange = {
        if (it.isEmpty() || it == "-") {
            onValueChange(0)
            text = it
            previousText = it
        } else try {
            onValueChange(it.toLong())
            text = it.toInt().toString()
            previousText = it
        } catch (_: Exception) {
            text = previousText
        }
    }, colors = colors)

}

@Composable
fun  IntTextField(value: Int, modifier: Modifier, colors: TextFieldColors, enabled: Boolean = true, label: @Composable ()->Unit, onValueChange: (Int) -> Unit) {
    var text by remember {mutableStateOf(value.toString(), neverEqualPolicy()) }
    var previousText by remember { mutableStateOf(text) }
    if ((previousText.isEmpty() || previousText == "-") && value == 0 && text != previousText)
        text = previousText
    else if (value != 0 && value.toString() != text)
        text = value.toString()
    else if (value == 0 && text != "0" && text != "-" && text.isNotEmpty())
        text = "0"
    OutlinedTextField(value = text , modifier = modifier, label = label, enabled = enabled, onValueChange = {
        if (it.isEmpty() || it == "-") {
            onValueChange(0)
            text = it
            previousText = it
        } else try {
            onValueChange(it.toInt())
            text = it.toInt().toString()
            previousText = it
        } catch (_: Exception) {
            text = previousText
        }
    }, colors = colors)

}
