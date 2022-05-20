package top.focess.mc.mi.ui.simulation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import aztech.modern_industrialization.machines.blockentities.hatches.NuclearHatch
import aztech.modern_industrialization.machines.components.NeutronHistoryComponent
import aztech.modern_industrialization.machines.components.TemperatureComponent
import aztech.modern_industrialization.nuclear.NuclearComponentItem
import top.focess.mc.mi.nuclear.NuclearSimulation
import top.focess.mc.mi.nuclear.mc.InputMatterHolder
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
                tint = MaterialTheme.colors.onBackground,
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
) = TooltipArea(tooltip = {
        Surface(
            modifier = Modifier.shadow(4.dp),
            color = MaterialTheme.colors.surface,
            shape = RoundedCornerShape(4.dp)
        ) {

            fun checkEmpty() :Boolean {
                if (!nuclearHatch.inventory.input.matterVariant.isBlank)
                    return false
                for (output in nuclearHatch.inventory.output)
                    if (!output.matterVariant.isBlank)
                        return false
                return true
            }

            if (checkEmpty())
                Text(
                    text = lang.get(
                        "simulation",
                        if (isStart) "nuclear-hatch-tooltip-disable" else "nuclear-hatch-tooltip-enable"
                    ),
                    modifier = DefaultTheme.defaultPadding(),
                    color = if (isStart) MaterialTheme.colors.error else MaterialTheme.colors.onBackground
                )
            else {
                val holder by mutableStateOf(nuclearHatch.inventory.input)
                val matterVariant by mutableStateOf(holder.matterVariant)
                val tag by mutableStateOf(holder.tag)
                val neutronHistory by mutableStateOf(nuclearHatch.neutronHistory)
                val temperatureComponent by mutableStateOf(nuclearHatch.nuclearReactorComponent)
                val output by mutableStateOf(nuclearHatch.inventory.output)
                val show = showName(lang, matterVariant, tag) + " - " +
                        showAmount(lang, holder) + "\n" +
                        showNeutronGeneration(lang, neutronHistory) + " - " +
                        showAvgEUGeneration(lang, neutronHistory) + "\n" +
                        showNeutronReceive(lang, neutronHistory) + " - " +
                        showNeutronFlux(lang, neutronHistory) + "\n" +
                        showTemperature(lang, holder, temperatureComponent) + "\n" +
                        showOutput(lang, output)
                Text(
                    text = show.substring(0,show.length - 1),
                    modifier = DefaultTheme.defaultPadding(),
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
    }) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colors.secondary)
                .clickable {
                    if (!isStart)
                        selectorState.newWindow(lang, x, y, nuclearHatch, updateNuclearHatch)
                }
        ) {
            val height by mutableStateOf(maxHeight)
            Column(Modifier.fillMaxWidth()) {
                InventoryView(lang, nuclearHatch.inventory)
                if (height < 200.dp)
                    NeutronView2(lang, nuclearHatch.neutronHistory)
                else
                    NeutronView(lang, nuclearHatch.neutronHistory)
                TemperatureView(lang, nuclearHatch.inventory.input, nuclearHatch.nuclearReactorComponent)
            }
        }
    }

@Composable
fun InventoryView(lang: Lang, inventory: MINuclearInventory) {
    Box(Modifier.fillMaxWidth().fillMaxHeight(0.6f)) {
        inventoryViewLayout(3, Modifier.fillMaxSize()) {
            InputView(lang, inventory.input)
            OutputView(lang, inventory.output)
        }
    }
}

@Composable
fun TemperatureView(lang: Lang, holder: InputMatterHolder, temperatureComponent: TemperatureComponent) {
    Box {
        Column {
            BoxWithConstraints {
                val matterVariant = holder.matterVariant
                val maxTemperature = if (!matterVariant.isBlank && matterVariant.matter is NuclearComponentItem) (matterVariant.matter as NuclearComponentItem).maxTemperature.toDouble() else temperatureComponent.temperatureMax
                if (maxHeight > 30.dp)
                    Row {
                        LinearProgressIndicator(
                            progress = (temperatureComponent.temperature / maxTemperature).toFloat(),
                            modifier = DefaultTheme.squarePadding().fillMaxWidth()
                        )
                    }
            }
            BoxWithConstraints {
                if (maxWidth > 150.dp && maxHeight > 50.dp)
                    Row {
                        Text(
                            text = showTemperature(lang, holder, temperatureComponent),
                            style = DefaultTheme.smallTextStyle(),
                            modifier = DefaultTheme.defaultPadding()
                        )
                    }
            }
        }
    }
}

@Composable
fun NeutronView(lang: Lang, neutronHistory: NeutronHistoryComponent) {
    BoxWithConstraints {
        val width by mutableStateOf(maxWidth)
        val height by mutableStateOf(maxHeight)
        Column {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = showNeutronGeneration(lang, neutronHistory),
                    style = DefaultTheme.smallerTextStyle(),
                    modifier = DefaultTheme.defaultPadding()
                )
                if (width > 300.dp)
                    Text(
                        text = showNeutronReceive(lang, neutronHistory),
                        style = DefaultTheme.smallerTextStyle(),
                        modifier = DefaultTheme.defaultPadding()
                    )
            }

            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = showAvgEUGeneration(lang, neutronHistory),
                    style = DefaultTheme.smallerTextStyle(),
                    modifier = DefaultTheme.defaultPadding()
                )
                if (width > 300.dp)
                    Text(
                        text = showNeutronFlux(lang, neutronHistory),
                        style = DefaultTheme.smallerTextStyle(),
                        modifier = DefaultTheme.defaultPadding()
                    )
            }
        }
    }
}

@Composable
fun NeutronView2(lang: Lang, neutronHistory: NeutronHistoryComponent) {
    BoxWithConstraints {
        val width by mutableStateOf(maxWidth)
        if (maxHeight > 30.dp)
        Column {
            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = showNeutronGeneration(lang, neutronHistory),
                    style = DefaultTheme.smallerTextStyle(),
                    modifier = DefaultTheme.defaultPadding()
                )
                if (width > 140.dp)
                Text(
                    text = showAvgEUGeneration(lang, neutronHistory),
                    style = DefaultTheme.smallerTextStyle(),
                    modifier = DefaultTheme.defaultPadding()
                )
            }

        }
    }
}