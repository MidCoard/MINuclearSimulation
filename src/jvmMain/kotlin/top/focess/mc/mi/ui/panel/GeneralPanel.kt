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
import top.focess.mc.mi.ui.lang.Lang
import top.focess.scheduler.Task
import top.focess.scheduler.ThreadPoolScheduler
import java.time.Duration

var tickTask : Task? = null

@Composable
fun GeneralPanel(lang: Lang, state: GlobalState) {
    val scheduler = ThreadPoolScheduler(1,false,"TickManager")

    Column {
        Row {
            Button(modifier = Modifier.align(Alignment.CenterVertically).padding(10.dp,5.dp),
                enabled = !state.isStart && state.simulation != null,
                onClick = {
                    tickTask = scheduler.runTimer(state.simulation!!::tick, Duration.ZERO,Duration.ofMillis(50))
                    state.isStart = true
                }) {
                Text(lang.get("general", "start"))
            }

            Button(modifier = Modifier.align(Alignment.CenterVertically),
                enabled = state.isStart, onClick = {
                    tickTask!!.cancel()
                    tickTask = null
                    state.isStart = false
                }) {
                Text(lang.get("general", "stop"))
            }
        }
    }
}