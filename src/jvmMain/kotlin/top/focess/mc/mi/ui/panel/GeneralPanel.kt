package top.focess.mc.mi.ui.panel

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.focess.mc.mi.nuclear.NuclearSimulation
import top.focess.mc.mi.ui.GlobalAction
import top.focess.mc.mi.ui.lang.Lang
import top.focess.scheduler.Task

@Composable
fun GeneralPanel(lang: Lang, isStart: Boolean, simulation: NuclearSimulation?, tickTask: Task?, action: GlobalAction) {
    Surface(Modifier.padding(10.dp, 5.dp)) {
        Row {
            Button(
                enabled = !isStart && simulation != null,
                onClick = {
                    action.start()
                }) {
                Text(lang.get("general", "start"))
            }

            Button(
                enabled = isStart && tickTask != null,
                onClick = {
                    action.stop()
                }) {
                Text(lang.get("general", "stop"))
            }
        }
    }
}