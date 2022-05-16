package top.focess.mc.mi.ui.simulation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import aztech.modern_industrialization.machines.blockentities.hatches.NuclearHatch
import top.focess.mc.mi.nuclear.mc.Fluid
import top.focess.mc.mi.nuclear.mc.Matter
import top.focess.mc.mi.nuclear.mc.MatterVariant
import top.focess.mc.mi.nuclear.mi.Texture
import top.focess.mc.mi.ui.GlobalState
import top.focess.mc.mi.ui.lang.Lang


val ROW_CELL_COUNT = 5

class SimulationSelectorState {
    val windows = mutableStateListOf<SimulationSelectorWindowState>()
    fun newWindow(lang: Lang, state: GlobalState, x:Int, y:Int) { windows.add(SimulationSelectorWindowState(lang, state, x, y, windows::remove)) }
}


class SimulationSelectorWindowState(
    val lang: Lang,
    val state: GlobalState,
    val x: Int,
    val y: Int,
    private val close: (SimulationSelectorWindowState) -> Unit
) {
    fun close() = close(this)
}

@Composable
fun simulationSelectorLayout(
    modifier: Modifier,
    how: Placeable.PlacementScope.(measurables: List<Measurable>, constraints: Constraints) -> Unit,
    children: @Composable () -> Unit)
        = Layout({ children() }, modifier) {measurables, constraints ->
    layout(constraints.maxWidth, constraints.maxHeight) {
        how(measurables, constraints)
    }
}

@Composable
fun simulationSelectorCellLayout(
    modifier: Modifier,
    how: Placeable.PlacementScope.(measurables: List<Measurable>, constraints: Constraints) -> Unit,
    children: @Composable () -> Unit)
        = Layout({ children() }, modifier) {measurables, constraints ->
    layout(constraints.maxWidth, constraints.maxHeight) {
        how(measurables, constraints)
    }
}

@Composable
fun simulationCell(matter: Matter, window: SimulationSelectorWindowState) {
    val texture = Texture.get(matter)
    val nuclearHatch = window.state.simulation!!.nuclearGrid.getNuclearTile(window.x,window.y).get() as NuclearHatch
    Box(modifier = Modifier.fillMaxSize().clickable {
        if (nuclearHatch.isFluid != matter is Fluid)
            window.state.simulation!!.nuclearGrid.setNuclearTile(window.x, window.y, NuclearHatch(matter is Fluid))
        nuclearHatch.inventory.input().matterVariant = MatterVariant.of(matter)
        window.close()
    }) {
        Image(
            bitmap = loadImageBitmap(texture.inputStream),
            matter.toString(),
            modifier = Modifier.fillMaxSize().align(Alignment.Center),
        )
        Text(matter.toString())
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun simulationSelector(window: SimulationSelectorWindowState) = Window(
    resizable = false,
    title = window.lang.get("simulation","selector","name"),
    onCloseRequest = { window.close() }
) {

    val nuclearHatch = window.state.simulation!!.nuclearGrid.getNuclearTile(window.x,window.y).get()
    simulationSelectorLayout(Modifier.fillMaxSize(), {
            measurables, constraints ->
        measurables[0].measure(
            Constraints(
                minHeight = constraints.maxHeight,
                maxHeight = constraints.maxHeight,
                minWidth = constraints.maxWidth / 3,
                maxWidth = constraints.maxWidth / 3
            )
        ).place(0,0)
        measurables[1].measure(
            Constraints(
                minHeight = constraints.maxHeight,
                maxHeight = constraints.maxHeight,
                minWidth = constraints.maxWidth * 2 / 3,
                maxWidth = constraints.maxWidth * 2 / 3
            )
        ).place(constraints.maxWidth / 3,0)
    }) {
        var isFluid by remember{ mutableStateOf(nuclearHatch.isFluid) }
        Column() {
            TooltipArea(tooltip = {
                Surface(
                    modifier = Modifier.shadow(4.dp),
                    color = Color(255, 255, 210),
                    shape = RoundedCornerShape(4.dp),
                ) {
                    if (nuclearHatch.isFluid)
                    Text(
                        text = window.lang.get("simulation", "selector", "tooltip", "item"),
                        modifier = Modifier.padding(10.dp),
                        color = Color.Red
                    )
                }
            }) {
                Button(
                    onClick = { isFluid = false },
                    enabled = isFluid,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    content = { Text(window.lang.get("simulation","selector","item")) },
                )
            }
            TooltipArea(tooltip = {
                Surface(
                    modifier = Modifier.shadow(4.dp),
                    color = Color(255, 255, 210),
                    shape = RoundedCornerShape(4.dp),
                ) {
                    if (nuclearHatch.isFluid.not())
                        Text(
                            text = window.lang.get("simulation", "selector", "tooltip", "fluid"),
                            modifier = Modifier.padding(10.dp),
                            color = Color.Red
                        )
                }
            }) {
                Button(
                    onClick = { isFluid = true },
                    enabled = isFluid.not(),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    content = { Text(window.lang.get("simulation", "selector", "fluid")) }
                )
            }
        }

        simulationSelectorCellLayout(Modifier.fillMaxSize(),{
            measurables, constraints ->
            val row = if (measurables.size % ROW_CELL_COUNT == 0) measurables.size / ROW_CELL_COUNT else measurables.size / ROW_CELL_COUNT + 1
            for (i in 0 until row) {
                for (j in 0 until ROW_CELL_COUNT) {
                    val index = i * ROW_CELL_COUNT + j
                    if (index < measurables.size) {
                        measurables[index].measure(
                            Constraints(
                                minHeight = constraints.maxHeight / row,
                                maxHeight = constraints.maxHeight / row,
                                minWidth = constraints.maxWidth / ROW_CELL_COUNT,
                                maxWidth = constraints.maxWidth / ROW_CELL_COUNT
                            )
                        ).place(j * constraints.maxWidth / ROW_CELL_COUNT, i * constraints.maxHeight / row)
                    }
                }
            }
        }) {

            if (isFluid.not())
                for (item in Constant.ITEMS) {
                    key(item) {
                        simulationCell(item, window)
                    }
                }

            if (isFluid)
                for (fluid in Constant.FLUIDS) {
                    key(fluid) {
                        simulationCell(fluid, window)
                    }
                }
        }
    }
}


