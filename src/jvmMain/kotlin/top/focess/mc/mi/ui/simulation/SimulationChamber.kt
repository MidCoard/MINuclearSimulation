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
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aztech.modern_industrialization.machines.blockentities.hatches.NuclearHatch
import aztech.modern_industrialization.machines.components.NeutronHistoryComponent
import aztech.modern_industrialization.machines.components.TemperatureComponent
import aztech.modern_industrialization.nuclear.NeutronType
import top.focess.mc.mi.nuclear.NuclearSimulation
import top.focess.mc.mi.nuclear.mc.MatterHolder
import top.focess.mc.mi.nuclear.mi.MINuclearInventory
import top.focess.mc.mi.nuclear.mi.Texture
import top.focess.mc.mi.ui.lang.Lang
import top.focess.mc.mi.ui.showTag
import top.focess.mc.mi.ui.substring
import top.focess.mc.mi.ui.theme.DefaultTheme

@Composable
fun SimulationChamber(
    lang: Lang,
    isStart: Boolean,
    selectorState: SimulationSelectorState,
    simulation: NuclearSimulation?,
    updateNuclearHatch: (Int, Int, NuclearHatch?) -> Unit
) {
    if (simulation != null)
        nuclearSimulationView(lang, isStart, selectorState, simulation, updateNuclearHatch)
    else
        emptyView(lang)

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
fun nuclearSimulationCellLayout(
    modifier: Modifier,
    how: Placeable.PlacementScope.(inventory: Measurable, neutron: Measurable, temperature: Measurable, constraints: Constraints) -> Unit,
    children: @Composable () -> Unit
) = Layout({ children() }, modifier) {
        measurables, constraints ->
    require(measurables.size == 3)
    layout(constraints.maxWidth, constraints.maxHeight) {
        how(measurables[0],measurables[1],measurables[2], constraints)
    }
}

@Composable
fun nuclearSimulationView(
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
            if (simulation.nuclearType.column.test(x))
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
                            if (simulation.nuclearGrid.getNuclearTile(x, y).isPresent) {
                                nuclearSimulationCell(
                                    lang,
                                    x, y,
                                    isStart,
                                    selectorState,
                                    simulation.nuclearGrid.getNuclearTile(x, y).get() as NuclearHatch
                                ) {
                                    updateNuclearHatch(x, y, it)
                                }
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun nuclearSimulationCell(
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
            nuclearSimulationCellLayout(
                Modifier.fillMaxSize(),
                {
                    inventory, neutron, temperature, constraints ->
                    inventory.measure(
                        constraints.copy(
                            minWidth = constraints.maxWidth,
                            maxWidth = constraints.maxWidth,
                            maxHeight = constraints.maxHeight * 7 / 12,
                            minHeight = constraints.maxHeight * 7 / 12
                        )
                    ).place(0, 0)
                    neutron.measure(
                        constraints.copy(
                            maxWidth = constraints.maxWidth,
                            minWidth = constraints.maxWidth,
                            maxHeight = constraints.maxHeight / 4,
                            minHeight = constraints.maxHeight / 4
                        )
                    ).place(0, constraints.maxHeight * 7 / 12)
                    temperature.measure(
                        constraints.copy(
                            maxWidth = constraints.maxWidth,
                            minWidth = constraints.maxWidth,
                            maxHeight = constraints.maxHeight / 6,
                            minHeight = constraints.maxHeight / 6
                        )
                    ).place(0, constraints.maxHeight * 5 / 6)
                }
            ) {
                InventoryView(lang, nuclearHatch.inventory)
                NeutronView(lang, nuclearHatch.neutronHistory)
                TemperatureView(lang, nuclearHatch.nuclearReactorComponent)
            }
        }
    }
}
@Composable
fun inventoryViewLayout(
    modifier: Modifier,
    how: Placeable.PlacementScope.(measurables: List<Measurable>, constraints: Constraints) -> Unit,
    children: @Composable () -> Unit
) = Layout({ children() }, modifier) {
        measurables, constraints ->
    layout(constraints.maxWidth, constraints.maxHeight) {
        how(measurables, constraints)
    }
}

@Composable
fun inputView(lang: Lang, holder: MatterHolder) {

    Box(Modifier.border(1.dp, DefaultTheme.inputBoarder).fillMaxSize()) {
        if (!holder.matterVariant.isBlank && holder.amount != 0L) {
            val texture = Texture.get(holder.matterVariant.matter!!)
            matterViewLayout(Modifier.fillMaxSize(),
                {
                        name, image, amount, constraints ->
                    name.measure(
                        Constraints(
                            minWidth = constraints.maxWidth,
                            maxWidth =  constraints.maxWidth,
                            minHeight =  constraints.maxHeight / 3,
                            maxHeight = constraints.maxHeight / 3
                        )
                    ).place(0,0)
                    image.measure(
                        Constraints(
                            minWidth = constraints.maxWidth,
                            maxWidth =  constraints.maxWidth,
                            minHeight =  constraints.maxHeight / 3,
                            maxHeight = constraints.maxHeight / 3
                        )
                    ).place(0,constraints.maxHeight/3)
                    amount.measure(
                        Constraints(
                            minWidth = constraints.maxWidth,
                            maxWidth =  constraints.maxWidth,
                            minHeight =  constraints.maxHeight / 3,
                            maxHeight = constraints.maxHeight / 3
                        )
                    ).place(0,constraints.maxHeight * 2/3)
                }) {
                Text(
                    lang.get("matter",holder.matterVariant.matter!!.namespace,holder.matterVariant.matter!!.name) + showTag(holder.matterVariant.tag),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(10.dp,5.dp)
                )
                Image(
                    bitmap = loadImageBitmap(texture.inputStream),
                    lang.get("simulation", "input"),
                    modifier = Modifier.align(Alignment.Center).fillMaxSize(),
                )
                Text(text = if(holder.isInfinite) lang.get("simulation","infinite") else holder.amount.toString(),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(10.dp,5.dp))
            }
        } else {
            Text(
                modifier = Modifier.align(Alignment.Center).padding(10.dp,5.dp),
                text = lang.get("simulation", "empty"),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun matterViewLayout(modifier: Modifier,how: Placeable.PlacementScope.(name:Measurable,image:Measurable,amount:Measurable, constraints: Constraints) -> Unit,children: @Composable () -> Unit) = Layout({ children() }, modifier) {
        measurables, constraints ->
    require(measurables.size == 3)
    layout(constraints.maxWidth, constraints.maxHeight) {
        how(measurables[0],measurables[1],measurables[2], constraints)
    }
}

@Composable
fun outputView(lang: Lang, holders: List<MatterHolder>) {
    for (holder in holders) {
        if (!holder.matterVariant.isBlank && holder.amount != 0L)
            Box(Modifier.border(1.dp, DefaultTheme.outputBoarder).fillMaxSize()) {
                val texture = Texture.get(holder.matterVariant.matter!!)
                matterViewLayout(Modifier.fillMaxSize(),
                    {
                        name, image, amount, constraints ->
                        name.measure(
                            Constraints(
                                minWidth = constraints.maxWidth,
                                maxWidth =  constraints.maxWidth,
                                minHeight =  constraints.maxHeight / 3,
                                maxHeight = constraints.maxHeight / 3
                            )
                        ).place(0,0)
                        image.measure(
                            Constraints(
                                minWidth = constraints.maxWidth,
                                maxWidth =  constraints.maxWidth,
                                minHeight =  constraints.maxHeight / 3,
                                maxHeight = constraints.maxHeight / 3
                            )
                        ).place(0,constraints.maxHeight/3)
                        amount.measure(
                            Constraints(
                                minWidth = constraints.maxWidth,
                                maxWidth =  constraints.maxWidth,
                                minHeight =  constraints.maxHeight / 3,
                                maxHeight = constraints.maxHeight / 3
                            )
                        ).place(0,constraints.maxHeight * 2/3)
                    }) {
                    Text(
                        lang.get("matter",holder.matterVariant.matter!!.namespace,holder.matterVariant.matter!!.name) + showTag(holder.tag),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(10.dp,5.dp)
                    )
                    Image(
                        bitmap = loadImageBitmap(texture.inputStream),
                        lang.get("simulation", "output"),
                        modifier = Modifier.align(Alignment.Center).fillMaxSize(),
                    )
                    Text(text = if(holder.isInfinite) lang.get("simulation","infinite") else holder.amount.toString(),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(10.dp,5.dp))
                }
            }
    }
}

const val ROW_VIEW_COUNT = 3

@Composable
fun InventoryView(lang: Lang, inventory: MINuclearInventory) {
    Box {
        inventoryViewLayout(Modifier.fillMaxSize(),{
            measurables, constraints ->
            val rowCount = if (measurables.size % ROW_VIEW_COUNT == 0) measurables.size / ROW_VIEW_COUNT else measurables.size / ROW_VIEW_COUNT + 1
            for (i in 0 until rowCount) {
                for (j in 0 until ROW_VIEW_COUNT) {
                    val index = i * ROW_VIEW_COUNT + j
                    val nowRowViewCount = if (i == rowCount - 1) measurables.size - i * ROW_VIEW_COUNT else ROW_VIEW_COUNT
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
            inputView(lang, inventory.input())
            outputView(lang, inventory.output)
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
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                )
            }
            Row {
                Text(
                    text = lang.get(
                        "simulation",
                        "temperature"
                    ) + ": " + (if (temperatureComponent.temperature.toString().length  > 6) temperatureComponent.temperature.toString()
                        .substring(0, 6) else temperatureComponent.temperature.toString()) + "/" + temperatureComponent.temperatureMax.toString(),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(10.dp, 5.dp)
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
                    text = lang.get(
                        "simulation",
                        "neutron", "generation"
                    ) + ": " + substring(neutronHistory.averageGeneration.toString(), 6),
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(10.dp, 5.dp)
                )
                Text(
                    text = lang.get(
                        "simulation",
                        "neutron", "receive"
                    ) + ": " + substring(neutronHistory.getAverageReceived(NeutronType.BOTH).toString(), 6)
                            + "(" + lang.get(
                        "simulation",
                        "neutron",
                        "fast"
                    ) + ": " + substring(neutronHistory.getAverageReceived(NeutronType.FAST).toString(), 6)
                            + "," + lang.get(
                        "simulation",
                        "neutron",
                        "thermal"
                    ) + ": " + substring(neutronHistory.getAverageReceived(NeutronType.THERMAL).toString(), 6) + ")",
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(10.dp, 5.dp)
                )
            }

            Row(Modifier.fillMaxWidth()) {
                Text(
                    text = lang.get(
                        "simulation",
                        "neutron", "flux"
                    ) + ": " + substring(neutronHistory.getAverageFlux(NeutronType.BOTH).toString(), 6)
                            + "(" + lang.get(
                        "simulation",
                        "neutron",
                        "fast"
                    ) + ": " + substring(neutronHistory.getAverageFlux(NeutronType.FAST).toString(), 6)
                            + "," + lang.get(
                        "simulation",
                        "neutron",
                        "thermal"
                    ) + ": " + substring(neutronHistory.getAverageFlux(NeutronType.THERMAL).toString(), 6) + ")",
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(10.dp, 5.dp)
                )
                Text(
                    text = lang.get(
                        "simulation",
                        "neutron", "eu-generation"
                    ) + ": " + substring(neutronHistory.averageEuGeneration.toString(), 6),
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(10.dp, 5.dp)
                )
            }
        }
    }
}