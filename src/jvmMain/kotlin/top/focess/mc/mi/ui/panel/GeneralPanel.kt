package top.focess.mc.mi.ui.panel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.focess.mc.mi.nuclear.NuclearSimulation
import top.focess.mc.mi.ui.GlobalAction
import top.focess.mc.mi.ui.lang.Lang
import top.focess.mc.mi.ui.textfield.IntTextField
import top.focess.mc.mi.ui.theme.DefaultTheme
import kotlin.math.roundToInt


const val MAX_TICK_RATE = 1000
const val MIN_TICK_RATE = 1

const val TICK_RATE_BASE = 1000f

@Composable
fun GeneralPanel(
    lang: Lang,
    isStart: Boolean,
    isInfinite: Boolean,
    simulation: NuclearSimulation?,
    tickRate: Int,
    action: GlobalAction,
    updateTickRate: (Int) -> Unit,
    updateInfinite: (Boolean) -> Unit
) {
    Surface(Modifier.padding(10.dp, 5.dp), color = MaterialTheme.colors.background) {
        Column {
            Row {
                Button(
                    modifier = Modifier.padding(5.dp),
                    enabled = !isStart && simulation != null,
                    onClick = {
                        action.start()
                    }) {
                    Text(lang.get("general", "start"))
                }

                Button(
                    modifier = Modifier.padding(5.dp),
                    enabled = isStart,
                    onClick = {
                        action.stop()
                    }) {
                    Text(lang.get("general", "stop"))
                }
            }

            val range = MIN_TICK_RATE / TICK_RATE_BASE..MAX_TICK_RATE / TICK_RATE_BASE

            Row {

                IntTextField(value = tickRate, modifier = DefaultTheme.defaultPadding(), onValueChange = {
                    updateTickRate(it.coerceAtLeast(1))
                }, colors = DefaultTheme.defaultTextField(), label = {
                    Text(lang.get("general", "tick-rate"))
                }, enabled = !isStart && !isInfinite)
            }

            Row {
                Slider(value = tickRate / TICK_RATE_BASE, valueRange = range, onValueChange = {
                    updateTickRate((it * 1000).roundToInt())
                }, colors = DefaultTheme.defaultSlider(), modifier = DefaultTheme.defaultPadding(), enabled = !isStart && !isInfinite)
            }

            Row {
                Checkbox(
                    checked = isInfinite,
                    onCheckedChange = { updateInfinite(it) },
                    modifier = Modifier.align(Alignment.CenterVertically),
                    colors = DefaultTheme.defaultCheckBox(),
                    enabled = !isStart
                )

                Text(
                    text = lang.get("general", "infinite"),
                    modifier = Modifier.align(Alignment.CenterVertically).clickable {
                        if (!isStart)
                            updateInfinite(!isInfinite)
                    },
                    style = DefaultTheme.defaultTextStyle()
                )
            }
        }
    }
}