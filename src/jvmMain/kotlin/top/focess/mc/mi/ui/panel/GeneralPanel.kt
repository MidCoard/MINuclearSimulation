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

@Composable
fun GeneralPanel(lang: Lang, state: GlobalState, action: GlobalAction) {

    Column {
        Row {
            Button(modifier = Modifier.align(Alignment.CenterVertically).padding(10.dp,5.dp),
                enabled = !state.isStart && state.simulation != null,
                onClick = {
                    action.start();
                }) {
                Text(lang.get("general", "start"))
            }

            Button(modifier = Modifier.align(Alignment.CenterVertically),
                enabled = state.isStart && state.tickTask != null,
                onClick = {
                    action.stop();
                }) {
                Text(lang.get("general", "stop"))
            }
        }
    }
}