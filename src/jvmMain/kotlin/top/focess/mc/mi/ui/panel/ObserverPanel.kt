package top.focess.mc.mi.ui.panel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.focess.mc.mi.nuclear.NuclearSimulation
import top.focess.mc.mi.ui.lang.Lang

@Composable
fun ObserverPanel(lang: Lang, simulation: NuclearSimulation?) {
    Column {
        Row {
            Text (lang.get("observer","tick") + ":" + (simulation?.tickCount ?: 0),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(10.dp,5.dp))
        }
    }
}