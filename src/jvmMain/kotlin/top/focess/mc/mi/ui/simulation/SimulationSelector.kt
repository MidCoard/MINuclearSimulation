package top.focess.mc.mi.ui.simulation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import aztech.modern_industrialization.machines.blockentities.hatches.NuclearHatch
import top.focess.mc.mi.nuclear.mc.*
import top.focess.mc.mi.nuclear.mi.Texture
import top.focess.mc.mi.ui.lang.Lang
import top.focess.mc.mi.ui.theme.DefaultTheme

const val ROW_CELL_COUNT = 5

class SimulationSelectorState {
    val windows = mutableStateListOf<SimulationSelectorWindowState>()
    fun newWindow(lang: Lang, x: Int, y: Int, nuclearHatch: NuclearHatch, updateNuclearHatch: (NuclearHatch?) -> Unit) {
        if (windows.firstOrNull { it.x == x && it.y == y } == null)
            windows.add(SimulationSelectorWindowState(lang, x, y, nuclearHatch, updateNuclearHatch, WindowState(size = DpSize(1200.dp, 900.dp)), windows::remove))
    }
}


class SimulationSelectorWindowState(
    val lang: Lang,
    val x: Int,
    val y: Int,
    val nuclearHatch: NuclearHatch,
    val updateNuclearHatch: (NuclearHatch?) -> Unit,
    val state: WindowState,
    private val close: (SimulationSelectorWindowState) -> Unit
) {
    fun close() = close(this)
}

@Composable
fun simulationSelectorLayout(
    modifier: Modifier,
    how: Placeable.PlacementScope.(measurables: List<Measurable>, constraints: Constraints) -> Unit,
    children: @Composable () -> Unit
) = Layout({ children() }, modifier) { measurables, constraints ->
    layout(constraints.maxWidth, constraints.maxHeight) {
        how(measurables, constraints)
    }
}

@Composable
fun simulationSelectorCellLayout(
    modifier: Modifier,
    how: Placeable.PlacementScope.(measurables: List<Measurable>, constraints: Constraints) -> Unit,
    children: @Composable () -> Unit
) = Layout({ children() }, modifier) { measurables, constraints ->
    layout(constraints.maxWidth, constraints.maxHeight) {
        how(measurables, constraints)
    }
}

@Composable
fun simulationCell(lang: Lang,matter: Matter, window: SimulationSelectorWindowState){
    val texture = Texture.get(matter)
    val nuclearHatch = window.nuclearHatch
    Box(modifier = Modifier.fillMaxSize()
        .background(DefaultTheme.simulationCell)
        .border(1.dp, DefaultTheme.simulationCellBoarder).clickable {
        if (nuclearHatch.isFluid != matter is Fluid) {
            val newNuclearHatch = NuclearHatch(matter is Fluid)
            window.updateNuclearHatch(newNuclearHatch)
            newNuclearHatch.inventory.input().matterVariant = MatterVariant.of(matter)
        } else nuclearHatch.inventory.input().matterVariant = MatterVariant.of(matter)
        window.close()
    }) {
        Column {
            simulationSelectorCellLayout(Modifier.fillMaxSize(), {
                    measurables, constraints ->
                measurables[0].measure(
                    Constraints(
                        minWidth = constraints.maxWidth,
                        maxWidth = constraints.maxWidth,
                        minHeight = constraints.maxHeight / 4,
                        maxHeight = constraints.maxHeight / 4
                    )
                ).place(0, 0)
                measurables[1].measure(
                    Constraints(
                        minWidth = constraints.maxWidth,
                        maxWidth = constraints.maxWidth,
                        minHeight = constraints.maxHeight * 3 / 4,
                        maxHeight = constraints.maxHeight * 3 / 4
                    )
                ).place(0, constraints.maxHeight / 4)
            }) {
                Text(
                    lang.get("matter", matter.namespace, matter.name),
                    color = LocalContentColor.current.copy(alpha = 0.60f),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(10.dp).fillMaxSize()
                )
                Image(
                    bitmap = loadImageBitmap(texture.inputStream),
                    matter.toString(),
                    modifier = Modifier.padding(20.dp,20.dp).fillMaxSize(1f)
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun simulationSelector(window: SimulationSelectorWindowState) = Window(
    resizable = false,
    title = window.lang.get("simulation", "selector", "name"),
    onCloseRequest = { window.close() },
    focusable = true,
    alwaysOnTop = true,
    state = window.state,
) {
    MaterialTheme(colors = DefaultTheme.default) {
        val nuclearHatch = window.nuclearHatch
        simulationSelectorLayout(Modifier.fillMaxSize(), { measurables, constraints ->
            measurables[0].measure(
                Constraints(
                    minHeight = constraints.maxHeight,
                    maxHeight = constraints.maxHeight,
                    minWidth = constraints.maxWidth / 3,
                    maxWidth = constraints.maxWidth / 3
                )
            ).place(0, 0)
            measurables[1].measure(
                Constraints(
                    minHeight = constraints.maxHeight,
                    maxHeight = constraints.maxHeight,
                    minWidth = constraints.maxWidth * 2 / 3,
                    maxWidth = constraints.maxWidth * 2 / 3
                )
            ).place(constraints.maxWidth / 3, 0)
        }) {
            var isFluid by remember { mutableStateOf(nuclearHatch.isFluid) }
            Column {
                Row {
                    TooltipArea(tooltip = {
                        Surface(
                            modifier = Modifier.shadow(4.dp),
                            color = Color(255, 255, 210),
                            shape = RoundedCornerShape(4.dp),
                        ) {
                            if (nuclearHatch.isFluid)
                                Text(
                                    text = window.lang.get("simulation", "selector", "tooltip", "fluid"),
                                    modifier = Modifier.padding(10.dp),
                                    color = Color.Red
                                )
                        }
                    }, modifier = Modifier.align(Alignment.CenterVertically).padding(10.dp, 5.dp)) {
                        Button(
                            onClick = { isFluid = false },
                            enabled = isFluid,
                            content = { Text(window.lang.get("simulation", "selector", "item")) },
                        )
                    }
                    TooltipArea(
                        tooltip = {
                            Surface(
                                modifier = Modifier.shadow(4.dp),
                                color = Color(255, 255, 210),
                                shape = RoundedCornerShape(4.dp),
                            ) {
                                if (nuclearHatch.isFluid.not())
                                    Text(
                                        text = window.lang.get("simulation", "selector", "tooltip", "item"),
                                        modifier = Modifier.padding(10.dp),
                                        color = Color.Red
                                    )
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterVertically).padding(10.dp, 5.dp),
                    ) {
                        Button(
                            onClick = { isFluid = true },
                            enabled = isFluid.not(),
                            content = { Text(window.lang.get("simulation", "selector", "fluid")) }
                        )
                    }

                    Button(
                        modifier = Modifier.align(Alignment.CenterVertically).padding(10.dp, 5.dp),
                        onClick = {
                            window.nuclearHatch.inventory.input().matterVariant = if (window.nuclearHatch.isFluid) FluidVariant.blank() else ItemVariant.blank()
                            window.close()
                        },
                        enabled = !nuclearHatch.inventory.input().matterVariant.isBlank,
                        content = { Text(window.lang.get("simulation", "selector", "clear")) }
                    )
                }
            }
            
            simulationSelectorCellLayout(Modifier.fillMaxSize(), { measurables, constraints ->
                val row =
                    if (measurables.size % ROW_CELL_COUNT == 0) measurables.size / ROW_CELL_COUNT else measurables.size / ROW_CELL_COUNT + 1
                for (i in 0 until row) {
                    for (j in 0 until ROW_CELL_COUNT) {
                        val index = i * ROW_CELL_COUNT + j
                        if (index < measurables.size) {
                            measurables[index].measure(
                                Constraints(
                                    minHeight = constraints.maxWidth / ROW_CELL_COUNT,
                                    maxHeight = constraints.maxWidth / ROW_CELL_COUNT,
                                    minWidth = constraints.maxWidth / ROW_CELL_COUNT,
                                    maxWidth = constraints.maxWidth / ROW_CELL_COUNT
                                )
                            ).place(j * constraints.maxWidth / ROW_CELL_COUNT, i * constraints.maxWidth / ROW_CELL_COUNT)
                        } else break
                    }
                }
            }) {

                if (!isFluid)
                    for (item in Constant.ITEMS) {
                        key(item) {
                            simulationCell(window.lang, item, window)
                        }
                    }
                else
                    for (fluid in Constant.FLUIDS) {
                        key(fluid) {
                            simulationCell(window.lang, fluid, window)
                        }
                    }
            }
        }
    }
}


