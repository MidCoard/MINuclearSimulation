package top.focess.mc.mi.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.focess.mc.mi.nuclear.NuclearSimulation
import top.focess.mc.mi.ui.lang.Lang
import top.focess.scheduler.Task

@Composable
fun GeneralPanel(lang: Lang, isStart: Boolean, simulation: NuclearSimulation?, tickTask: Task?, action: GlobalAction) {

    Column {
        Row {
            Button(modifier = Modifier.align(Alignment.CenterVertically).padding(10.dp, 5.dp),
                enabled = !isStart && simulation != null,
                onClick = {
                    action.start()
                }) {
                Text(lang.get("general", "start"))
            }

            Button(modifier = Modifier.align(Alignment.CenterVertically),
                enabled = isStart && tickTask != null,
                onClick = {
                    action.stop()
                }) {
                Text(lang.get("general", "stop"))
            }
        }
    }
}