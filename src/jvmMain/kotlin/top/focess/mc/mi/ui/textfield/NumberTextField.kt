package top.focess.mc.mi.ui.textfield

import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun  LongTextField(value: Long, modifier: Modifier, colors: TextFieldColors , enabled: Boolean = true,label: @Composable ()->Unit, onValueChange: (Long) -> Unit) {
    var text by remember { mutableStateOf(value.toString(), neverEqualPolicy()) }
    var previousText = text
    OutlinedTextField(value = text , modifier = modifier, label = label, enabled = enabled, onValueChange = {
        if (it.isEmpty() || it == "-") {
            onValueChange(0)
            text = it
        } else try {
            onValueChange(it.toLong())
            text = it.toLong().toString()
            previousText = text
        } catch (_: Exception) {
            text = previousText
        }
    }, colors = colors)

}

@Composable
fun  IntTextField(value: Int, modifier: Modifier, colors: TextFieldColors, enabled: Boolean = true, label: @Composable ()->Unit, onValueChange: (Int) -> Unit) {
    var text by remember { mutableStateOf(value.toString(), neverEqualPolicy()) }
    var previousText = text
    OutlinedTextField(value = text , modifier = modifier, label = label, enabled = enabled, onValueChange = {
        if (it.isEmpty() || it == "-") {
            onValueChange(0)
            text = it
        } else try {
            onValueChange(it.toInt())
            text = it.toInt().toString()
            previousText = text
        } catch (_: Exception) {
            text = previousText
        }
    }, colors = colors)

}
