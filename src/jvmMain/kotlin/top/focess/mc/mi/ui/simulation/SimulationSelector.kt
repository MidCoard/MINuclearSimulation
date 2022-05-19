package top.focess.mc.mi.ui.simulation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import aztech.modern_industrialization.machines.blockentities.hatches.NuclearHatch
import top.focess.mc.mi.nuclear.mc.*
import top.focess.mc.mi.nuclear.mi.Texture
import top.focess.mc.mi.ui.lang.Lang
import top.focess.mc.mi.ui.textfield.IntTextField
import top.focess.mc.mi.ui.textfield.LongTextField
import top.focess.mc.mi.ui.theme.DefaultTheme

class SimulationSelectorState {
    val windows = mutableStateListOf<SimulationSelectorWindowState>()
    fun newWindow(lang: Lang, x: Int, y: Int, nuclearHatch: NuclearHatch, updateNuclearHatch: (NuclearHatch?) -> Unit) {
        if (windows.firstOrNull { it.x == x && it.y == y } == null)
            windows.add(
                SimulationSelectorWindowState(
                    lang,
                    x,
                    y,
                    nuclearHatch,
                    updateNuclearHatch,
                    WindowState(size = DpSize(1200.dp, 900.dp)),
                    windows::remove
                )
            )
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


// the top two is used to manage the selector windows

@Composable
fun simulationSelectorCellLayout(
    countPerRow: Int,
    modifier: Modifier,
    children: @Composable () -> Unit
) = Layout({ children() }, modifier) { measurables, constraints ->
    layout(constraints.maxWidth, constraints.maxHeight) {
        val row =
            if (measurables.size % countPerRow == 0) measurables.size / countPerRow else measurables.size / countPerRow + 1
        for (i in 0 until row) {
            for (j in 0 until countPerRow) {
                val index = i * countPerRow + j
                if (index < measurables.size) {
                    measurables[index].measure(
                        Constraints(
                            minHeight = constraints.maxWidth / countPerRow,
                            maxHeight = constraints.maxWidth / countPerRow,
                            minWidth = constraints.maxWidth / countPerRow,
                            maxWidth = constraints.maxWidth / countPerRow
                        )
                    ).place(
                        j * constraints.maxWidth / countPerRow,
                        i * constraints.maxWidth / countPerRow
                    )
                } else break
            }
        }
    }
}

@Composable
fun SimulationCell(lang: Lang, matter: Matter, selected: Boolean, updateMatterVariant: (MatterVariant) -> Unit) {
    val texture = Texture.get(matter)
    Box(modifier = DefaultTheme.selectedBorderAndBackground(selected).fillMaxSize()
        .clickable {
            updateMatterVariant(MatterVariant.of(matter))
        }) {
        Column {

            Row {
                Text(
                    lang.get("matter", matter.namespace, matter.name),
                    style = DefaultTheme.midSmallTextStyle(),
                    modifier = DefaultTheme.defaultPadding()
                )
            }
            Box {
                Image(
                    bitmap = loadImageBitmap(texture.inputStream),
                    contentDescription = matter.toString(),
                    modifier = DefaultTheme.squarePadding().fillMaxSize()
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun SelectorOverview(
    lang: Lang,
    nuclearHatch: NuclearHatch,
    isFluid: Boolean,
    matterVariant: MatterVariant,
    amount: Long,
    updateIsFluid: (Boolean) -> Unit,
    updateAmount: (Long) -> Unit,
    updateNuclearHatch: (NuclearHatch) -> Unit,
    closeWindow: () -> Unit
) {
    var isInfinite by remember { mutableStateOf(nuclearHatch.inventory.input.isInfinite) }
    var outputCount by remember { mutableStateOf(nuclearHatch.inventory.outputCount) }
    val inventory = nuclearHatch.inventory


    Column {

        Row {
            TooltipArea(
                tooltip = {
                    Surface(
                        modifier = Modifier.shadow(4.dp),
                        color = MaterialTheme.colors.surface,
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        if (nuclearHatch.isFluid)
                            Text(
                                text = lang.get("simulation", "selector", "tooltip", "fluid"),
                                modifier = DefaultTheme.defaultPadding(),
                                color = MaterialTheme.colors.error
                            )
                    }
                },
                modifier = DefaultTheme.defaultPadding().align(Alignment.CenterVertically)
            ) {
                Button(
                    onClick = { updateIsFluid(false) },
                    enabled = isFluid,
                    content = { Text(lang.get("simulation", "selector", "item")) },
                )
            }
            TooltipArea(
                tooltip = {
                    Surface(
                        modifier = Modifier.shadow(4.dp),
                        color = MaterialTheme.colors.surface,
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        if (!nuclearHatch.isFluid)
                            Text(
                                text = lang.get("simulation", "selector", "tooltip", "item"),
                                modifier = DefaultTheme.defaultPadding(),
                                color = MaterialTheme.colors.error
                            )
                    }
                },
                modifier = DefaultTheme.defaultPadding().align(Alignment.CenterVertically)
            ) {
                Button(
                    onClick = { updateIsFluid(true) },
                    enabled = !isFluid,
                    content = { Text(lang.get("simulation", "selector", "fluid")) }
                )
            }
        }

        Row {

            LongTextField(
                value = amount,
                modifier = DefaultTheme.defaultPadding().align(Alignment.CenterVertically),
                colors = DefaultTheme.defaultTextField(),
                enabled = !isInfinite,
                label = { Text(lang.get("simulation", "selector", "input-amount")) },
                onValueChange = {
                    updateAmount(it.coerceAtLeast(0))
                }
            )

            Checkbox(
                checked = isInfinite,
                onCheckedChange = { isInfinite = it },
                modifier = Modifier.align(Alignment.CenterVertically),
                colors = DefaultTheme.defaultCheckBox()
            )

            Text(
                text = lang.get("simulation", "selector", "infinite"),
                modifier = Modifier.align(Alignment.CenterVertically),
                style = DefaultTheme.defaultTextStyle()
            )
        }

        Row {

            IntTextField(value = outputCount,
                modifier = DefaultTheme.defaultPadding().align(Alignment.CenterVertically),
                label = { Text(lang.get("simulation", "selector", "output-count")) },
                onValueChange = {
                    inventory.outputCount = it.coerceAtLeast(0).coerceAtMost(10)
                    outputCount = it.coerceAtLeast(0).coerceAtMost(10)
                },
                colors = DefaultTheme.defaultTextField()
            )

        }

        Box(Modifier.fillMaxHeight(0.5f)) {
            val listState = rememberLazyListState()
            LazyColumn(state = listState) {
                items(items = inventory.output) {
                    OutputStatus(lang, it)
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = listState
                ),
                style = DefaultTheme.defaultScrollbar()
            )
        }

        Row {
            Button(
                modifier = DefaultTheme.defaultPadding().align(Alignment.CenterVertically),
                onClick = {
                    inventory.input.setMatterVariant(
                        false,
                        if (isFluid) FluidVariant.blank() else ItemVariant.blank(),
                        0
                    )
                    closeWindow()
                },
                content = { Text(lang.get("simulation", "selector", "empty-input")) }
            )

            Button(
                modifier = DefaultTheme.defaultPadding().align(Alignment.CenterVertically),
                onClick = {
                    for (holder in inventory.output)
                        holder.empty()
                    closeWindow()
                },
                content = { Text(lang.get("simulation", "selector", "empty-output")) }
            )
        }

        Row {
            Button(
                modifier = DefaultTheme.defaultPadding().align(Alignment.CenterVertically),
                onClick = {
                    val hatch =
                        if (matterVariant is FluidVariant == nuclearHatch.isFluid) nuclearHatch else NuclearHatch(
                            matterVariant is FluidVariant
                        )
                    if (matterVariant is FluidVariant != nuclearHatch.isFluid) {
                        hatch.inventory.outputCount = inventory.outputCount
                        for (i in 0 until hatch.inventory.outputCount) {
                            val holder = hatch.inventory.output[i]
                            val targetHolder = inventory.output[i]
                            holder.equalOutputMaxAmount = targetHolder.equalOutputMaxAmount
                            holder.equalTakeout = targetHolder.equalTakeout
                        }
                    }
                    if (hatch.isFluid)
                        hatch.inventory.input.setMatterVariant(isInfinite, matterVariant, amount * 81L)
                    else hatch.inventory.input.setMatterVariant(isInfinite, matterVariant, amount)
                    updateNuclearHatch(hatch)
                    closeWindow()
                },
                content = { Text(lang.get("simulation", "selector", "confirm")) }
            )

            Button(
                modifier = DefaultTheme.defaultPadding().align(Alignment.CenterVertically),
                onClick = { closeWindow() },
                content = { Text(lang.get("simulation", "selector", "cancel")) }
            )
        }
    }
}


@Composable
fun SimulationSelector(window: SimulationSelectorWindowState) = Window(
    resizable = false,
    title = window.lang.get("simulation", "selector", "name"),
    onCloseRequest = { window.close() },
    focusable = true,
    state = window.state,
    alwaysOnTop = true,
) {

    val nuclearHatch = window.nuclearHatch
    var matterVariant by remember { mutableStateOf(nuclearHatch.inventory.input.matterVariant) }
    var amount by remember { mutableStateOf(nuclearHatch.inventory.input.equalAmount) }
    var isFluid by remember { mutableStateOf(nuclearHatch.isFluid) }

    MaterialTheme(colors = DefaultTheme.getDefault()) {

        Row(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            Column(Modifier.fillMaxWidth(0.4f)) {
                SelectorOverview(
                    window.lang,
                    nuclearHatch,
                    isFluid,
                    matterVariant,
                    amount,
                    { isFluid = it },
                    { amount = it },
                    { window.updateNuclearHatch(it) },
                    { window.close() })
            }

            Column {
                simulationSelectorCellLayout(5, Modifier.fillMaxSize()) {
                    if (!isFluid)
                        for (item in Constant.ITEMS)
                            key(item) {
                                SimulationCell(window.lang, item, matterVariant.matter == item) {
                                    matterVariant = it
                                    if (amount == 0L)
                                        amount = 1
                                }
                            }
                    else
                        for (fluid in Constant.FLUIDS)
                            key(fluid) {
                                SimulationCell(window.lang, fluid, matterVariant.matter == fluid) {
                                    matterVariant = it
                                    if (amount == 0L)
                                        amount = 1000
                                }
                            }
                }
            }
        }
    }
}

@Composable
fun OutputStatus(lang: Lang, output: OutputMatterHolder) {
    var takeout by remember { mutableStateOf(output.equalTakeout) }
    var maxAmount by remember { mutableStateOf(output.equalOutputMaxAmount) }
    Row {
        LongTextField(
           value = maxAmount,
            modifier = DefaultTheme.defaultPadding().align(Alignment.CenterVertically).fillMaxWidth(0.5f),
            onValueChange = {
                maxAmount = it.coerceAtLeast(-1)
                output.equalOutputMaxAmount = it.coerceAtLeast(-1)
            },
            label = {Text(lang.get("simulation", "selector", "max-count"))},
            colors = DefaultTheme.defaultTextField()
        )

        LongTextField(
            value = takeout,
            modifier = DefaultTheme.defaultPadding().align(Alignment.CenterVertically),
            onValueChange = {
                takeout = it.coerceAtLeast(0)
                output.equalTakeout = it.coerceAtLeast(0)
            },
            colors = DefaultTheme.defaultTextField(),
            label = {Text(text = lang.get("simulation", "selector", "takeout"))}
        )
    }
}


