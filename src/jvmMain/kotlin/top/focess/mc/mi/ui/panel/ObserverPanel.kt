package top.focess.mc.mi.ui.panel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.focess.mc.mi.nuclear.NuclearSimulation
import top.focess.mc.mi.nuclear.mi.MINuclearInventory
import top.focess.mc.mi.ui.lang.Lang
import top.focess.mc.mi.ui.theme.DefaultTheme
import top.focess.mc.mi.ui.view.OutputView
import top.focess.mc.mi.ui.view.inventoryViewLayout

@Composable
fun ObserverPanel(
    lang: Lang,
    simulation: NuclearSimulation?,
    itemInventory: MINuclearInventory,
    fluidInventory: MINuclearInventory
) {
    Surface(DefaultTheme.defaultPadding(), color = MaterialTheme.colors.background) {

        Column {

            Row {
                Text(
                    lang.get("observer", "tick") + ":" + (simulation?.tickCount ?: 0),
                    style = DefaultTheme.defaultTextStyle(),
                    modifier = Modifier.padding(10.dp, 5.dp)
                )
            }

            Row {

                Button(modifier = Modifier.padding(10.dp, 5.dp),
                    onClick = {
                        for (item in itemInventory.output)
                            item.empty()
                        for (fluid in fluidInventory.output)
                            fluid.empty()
                    }) {
                    Text(lang.get("observer", "clear"))
                }
            }

            Row {
                inventoryViewLayout(2,Modifier.fillMaxSize()) {
                    OutputView(lang, itemInventory.output)
                    OutputView(lang, fluidInventory.output)
                }
            }
        }

    }
}