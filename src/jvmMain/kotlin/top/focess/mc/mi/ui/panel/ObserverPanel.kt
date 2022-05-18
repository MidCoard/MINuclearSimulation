package top.focess.mc.mi.ui.panel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.focess.mc.mi.nuclear.NuclearSimulation
import top.focess.mc.mi.nuclear.mi.MINuclearInventory
import top.focess.mc.mi.ui.lang.Lang
import top.focess.mc.mi.ui.simulation.inventoryViewLayout
import top.focess.mc.mi.ui.simulation.outputView

const val ROW_VIEW_COUNT2 = 2

@Composable
fun ObserverPanel(lang: Lang, simulation: NuclearSimulation?, itemInventory: MINuclearInventory, fluidInventory: MINuclearInventory) {
    Column {
        Row {
            Text (lang.get("observer","tick") + ":" + (simulation?.tickCount ?: 0),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(10.dp,5.dp))
        }

        Row {

            Button(modifier = Modifier.align(Alignment.CenterVertically).padding(10.dp, 5.dp),
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
            inventoryViewLayout(Modifier.fillMaxSize(),{
                    measurables, constraints ->
                val rowCount = if (measurables.size % ROW_VIEW_COUNT2 == 0) measurables.size / ROW_VIEW_COUNT2 else measurables.size / ROW_VIEW_COUNT2 + 1
                for (i in 0 until rowCount) {
                    for (j in 0 until ROW_VIEW_COUNT2) {
                        val index = i * ROW_VIEW_COUNT2 + j
                        val nowRowViewCount = if (i == rowCount - 1) measurables.size - i * ROW_VIEW_COUNT2 else ROW_VIEW_COUNT2
                        if (index < measurables.size) {
                            measurables[index].measure(
                                Constraints(
                                    minWidth = constraints.maxWidth / nowRowViewCount,
                                    maxWidth = constraints.maxWidth / nowRowViewCount,
                                    minHeight = (constraints.maxWidth / nowRowViewCount).coerceAtMost(constraints.maxHeight / rowCount),
                                    maxHeight = (constraints.maxWidth / nowRowViewCount).coerceAtMost(constraints.maxHeight / rowCount)
                                )
                            ).place(
                                j * constraints.maxWidth / nowRowViewCount,
                                i * (constraints.maxWidth / nowRowViewCount).coerceAtMost(constraints.maxHeight / rowCount)
                            )
                        } else break
                    }
                }
            }) {
                outputView(lang, itemInventory.output)
                outputView(lang, fluidInventory.output)
            }
        }

    }
}