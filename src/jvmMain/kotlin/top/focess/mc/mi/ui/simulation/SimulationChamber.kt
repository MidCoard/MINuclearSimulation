package top.focess.mc.mi.ui.simulation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import aztech.modern_industrialization.machines.blockentities.hatches.NuclearHatch
import aztech.modern_industrialization.machines.components.NeutronHistoryComponent
import aztech.modern_industrialization.machines.components.TemperatureComponent
import top.focess.mc.mi.nuclear.NuclearSimulation
import top.focess.mc.mi.nuclear.mi.MINuclearInventory
import top.focess.mc.mi.ui.*
import top.focess.mc.mi.ui.lang.Lang
import top.focess.mc.mi.ui.theme.DefaultTheme
import top.focess.mc.mi.ui.view.InputView
import top.focess.mc.mi.ui.view.OutputView
import top.focess.mc.mi.ui.view.inventoryViewLayout

@Composable
fun SimulationChamber(
    lang: Lang,
    isStart: Boolean,
    selectorState: SimulationSelectorState,
    simulation: NuclearSimulation?,
    updateNuclearHatch: (Int, Int, NuclearHatch?) -> Unit
) {
    if (simulation != null)
        NuclearSimulationView(lang, isStart, selectorState, simulation, updateNuclearHatch)
    else
        EmptyView(lang)

}

@Composable
fun EmptyView(lang: Lang) {
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.align(Alignment.Center)) {
            Icon(
                Icons.Default.Info,
                contentDescription = "No Opened Simulation",
                tint = LocalContentColor.current.copy(alpha = 0.60f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                lang.get("simulation", "no-simulation"),
                style = DefaultTheme.defaultTextStyle(),
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
            )
        }
    }
}

@Composable
fun nuclearSimulationLayout(
    modifier: Modifier,
    how: Placeable.PlacementScope.(measurables: List<Measurable>, constraints: Constraints) -> Unit,
    children: @Composable () -> Unit
) = Layout({ children() }, modifier) { measurables, constraints ->
    layout(constraints.maxWidth, constraints.maxHeight) {
        how(measurables, constraints)
    }
}

@Composable
fun NuclearSimulationView(
    lang: Lang,
    isStart: Boolean,
    selectorState: SimulationSelectorState,
    simulation: NuclearSimulation,
    updateNuclearHatch: (Int, Int, NuclearHatch?) -> Unit
) {
    nuclearSimulationLayout(Modifier.fillMaxSize(), { measurables, constraints ->
        measurables.forEachIndexed { index, measurable ->
            measurable.measure(
                Constraints(
                    minWidth = constraints.maxWidth / measurables.size,
                    maxWidth = constraints.maxWidth / measurables.size,
                    minHeight = constraints.maxHeight,
                    maxHeight = constraints.maxHeight
                )
            ).place(index * constraints.maxWidth / measurables.size, 0)
        }
    }) {
        // one row and n Columns
        // each Column has n elements
        repeat(simulation.nuclearType.size) { x: Int ->
            if (simulation.nuclearType.limitation.test(x))
                Column {
                    nuclearSimulationLayout(Modifier.fillMaxSize(), { measurables, constraints ->
                        measurables.forEachIndexed { index, measurable ->
                            measurable.measure(
                                Constraints(
                                    minWidth = constraints.maxWidth,
                                    maxWidth = constraints.maxWidth,
                                    minHeight = constraints.maxHeight / measurables.size,
                                    maxHeight = constraints.maxHeight / measurables.size
                                )
                            ).place(0, constraints.maxHeight / measurables.size * index)
                        }
                    }) {
                        repeat(simulation.nuclearType.size) { y: Int ->
                            if (simulation.nuclearType.limitation.test(y))
                            if (simulation.nuclearGrid.getNuclearTile(x, y).isPresent) {
                                NuclearSimulationCell(
                                    lang,
                                    x, y,
                                    isStart,
                                    selectorState,
                                    simulation.nuclearGrid.getNuclearTile(x, y).get() as NuclearHatch
                                ) {
                                    updateNuclearHatch(x, y, it)
                                }
                            } else EmptyNuclearSimulationCell()
                        }
                    }
                }
        }
    }
}

@Composable
fun EmptyNuclearSimulationCell() {
    Box(Modifier.fillMaxSize()) {}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NuclearSimulationCell(
    lang: Lang,
    x: Int,
    y: Int,
    isStart: Boolean,
    selectorState: SimulationSelectorState,
    nuclearHatch: NuclearHatch,
    updateNuclearHatch: (NuclearHatch?) -> Unit
) = Surface {
    TooltipArea(tooltip = {
        Surface(
            modifier = Modifier.shadow(4.dp),
            color = Color(255, 255, 210),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(
                text = lang.get(
                    "simulation",
                    if (isStart) "nuclear-hatch-tooltip-disable" else "nuclear-hatch-tooltip-enable"
                ),
                modifier = Modifier.padding(10.dp),
                color = if (isStart) Color.Red else Color.Black
            )
        }
    }) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(DefaultTheme.simulationCell)
                .border(1.dp, DefaultTheme.simulationCellBoarder)
                .clickable {
                    if (!isStart)
                        selectorState.newWindow(lang, x, y, nuclearHatch, updateNuclearHatch)
                }
        ) {
            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxHeight(0.6f)){
                    InventoryView(lang, nuclearHatch.inventory)
                }
                Row(Modifier.fillMaxHeight(0.5f)) {
                    NeutronView(lang, nuclearHatch.neutronHistory)
                }
                Row {
                    TemperatureView(lang, nuclearHatch.nuclearReactorComponent)
                }
            }
        }
    }
}

@Composable
fun InventoryView(lang: Lang, inventory: MINuclearInventory) {
    Box {
        inventoryViewLayout(3, Modifier.fillMaxSize()) {
            InputView(lang, inventory.input)
            OutputView(lang, inventory.output)
        }
    }
}

@Composable
fun TemperatureView(lang: Lang, temperatureComponent: TemperatureComponent) {
    Box {
        Column {
            Row {
                LinearProgressIndicator(
                    progress = (temperatureComponent.temperature / temperatureComponent.temperatureMax).toFloat(),
                    modifier = Modifier.padding(10.dp).fillMaxWidth()
                )
            }
            Row {
                Text(
                    text = showTemperature(lang, temperatureComponent),
                    style = DefaultTheme.smallTextStyle(),
                    modifier = DefaultTheme.defaultPadding()
                )
            }
        }
    }
}

@Composable
fun NeutronView(lang: Lang, neutronHistory: NeutronHistoryComponent) {
    Box {
        Column {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = showNeutronGeneration(lang, neutronHistory),
                    style = DefaultTheme.smallerTextStyle(),
                    modifier = DefaultTheme.defaultPadding()
                )
                Text(
                    text = showNeutronReceive(lang, neutronHistory),
                    style = DefaultTheme.smallerTextStyle(),
                    modifier = DefaultTheme.defaultPadding()
                )
            }

            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = showNeutronFlux(lang, neutronHistory),
                    style = DefaultTheme.smallerTextStyle(),
                    modifier = DefaultTheme.defaultPadding()
                )
                Text(
                    text = showAvgEUGeneration(lang, neutronHistory),
                    style = DefaultTheme.smallerTextStyle(),
                    modifier = DefaultTheme.defaultPadding()
                )
            }
        }
    }
}