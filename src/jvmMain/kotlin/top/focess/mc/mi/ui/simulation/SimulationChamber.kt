package top.focess.mc.mi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aztech.modern_industrialization.machines.blockentities.hatches.NuclearHatch
import top.focess.mc.mi.nuclear.NuclearSimulation
import top.focess.mc.mi.ui.lang.Lang
import top.focess.mc.mi.ui.theme.DefaultTheme

@Composable
fun SimulationChamber(lang: Lang, simulation: NuclearSimulation?) {
    if (simulation != null)
        nuclearSimulationView(simulation)
    else
        emptyView(lang)

}

@Composable
fun nuclearSimulationLayout(
    modifier: Modifier,
    how: Placeable.PlacementScope.(measurables:List<Measurable>, constraints:Constraints) -> Unit,
    children: @Composable () -> Unit)
= Layout({ children() }, modifier) {measurables, constraints ->
    layout(constraints.maxWidth, constraints.maxHeight) {
        how(measurables, constraints)
    }
}

@Composable
fun nuclearSimulationView(simulation: NuclearSimulation) {
    nuclearSimulationLayout(Modifier.fillMaxSize(), {measurables, constraints ->
        measurables.forEachIndexed { index, measurable ->
            measurable.measure(Constraints(
                minWidth = constraints.maxWidth / measurables.size,
                maxWidth = constraints.maxWidth / measurables.size,
                minHeight = constraints.maxHeight,
                maxHeight = constraints.maxHeight
            )).place(index * constraints.maxWidth / measurables.size,0)
        }
    }) {
        // one row and n Columns
        // each Column has n elements
        repeat(simulation.nuclearType.size) { x: Int ->
            if (simulation.nuclearType.column.test(x))
                Column() {
                    nuclearSimulationLayout(Modifier.fillMaxSize(), {
                            measurables, constraints ->
                        measurables.forEachIndexed { index, measurable ->
                            measurable.measure(Constraints(
                                minWidth = constraints.maxWidth,
                                maxWidth = constraints.maxWidth,
                                minHeight = constraints.maxHeight / measurables.size,
                                maxHeight = constraints.maxHeight / measurables.size
                            )).place(0, constraints.maxHeight / measurables.size  * index)
                        }
                    }) {
                        repeat(simulation.nuclearType.size) { y: Int ->
                            if (simulation.nuclearGrid.getNuclearTile(x, y).isPresent) {
                                nuclearSimulationCell(
                                    simulation.nuclearGrid.getNuclearTile(x, y).get() as NuclearHatch,
                                )
                            }
                        }
                    }
                }
        }
    }
}

@Composable
fun emptyView(lang: Lang) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.align(Alignment.Center)) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = LocalContentColor.current.copy(alpha = 0.60f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                lang.get("simulation", "no-simulation"),
                color = LocalContentColor.current.copy(alpha = 0.60f),
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
            )
        }
    }
}

@Composable
fun nuclearSimulationCell(nuclearHatch: NuclearHatch) = Surface {
    Box(
        modifier = Modifier.fillMaxSize().background(DefaultTheme.simulationCell)
            .border(1.dp, LocalContentColor.current.copy(alpha = 0.60f))
    ) {
        Text("hello")

    }
}
