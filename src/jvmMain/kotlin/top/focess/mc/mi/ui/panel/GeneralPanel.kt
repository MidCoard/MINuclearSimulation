package top.focess.mc.mi.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import top.focess.mc.mi.ui.lang.Lang

@Composable
fun GeneralPanel(lang: Lang) {
    Column {

        Button(modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
            }) {
            Text(lang.get("general","start"))
        }
    }
}