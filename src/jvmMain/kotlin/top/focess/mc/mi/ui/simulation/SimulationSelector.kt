package top.focess.mc.mi.ui.simulation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import aztech.modern_industrialization.machines.blockentities.hatches.NuclearHatch
import top.focess.mc.mi.nuclear.mc.*
import top.focess.mc.mi.nuclear.mi.Texture
import top.focess.mc.mi.ui.lang.Lang
import top.focess.mc.mi.ui.theme.DefaultTheme

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
fun SimulationCell(lang: Lang, matter: Matter, selected: Boolean, updateMatterVariant: (MatterVariant) -> Unit){
    val texture = Texture.get(matter)
    Box(modifier = DefaultTheme.selectedBorder(selected).fillMaxSize()
        .background(MaterialTheme.colors.secondary)
        .clickable {
            updateMatterVariant(MatterVariant.of(matter))
    }) {
        Column {

            Row(Modifier.fillMaxHeight(0.25f)) {
                Text(
                    lang.get("matter", matter.namespace, matter.name),
                    style = DefaultTheme.smallerTextStyle(),
                    modifier = DefaultTheme.defaultPadding()
                )
            }
            Box {
                Image(
                    bitmap = loadImageBitmap(texture.inputStream),
                    contentDescription = matter.toString(),
                    modifier = DefaultTheme.defaultPadding().fillMaxSize(1f)
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
    Column {

        Row {
            TooltipArea(tooltip = {
                Surface(
                    modifier = Modifier.shadow(4.dp),
                    color = Color(255, 255, 210),
                    shape = RoundedCornerShape(4.dp),
                ) {
                    if (isFluid)
                        Text(
                            text = lang.get("simulation", "selector", "tooltip", "fluid"),
                            modifier = DefaultTheme.defaultPadding(),
                            color = MaterialTheme.colors.error
                        )
                } },
                modifier = DefaultTheme.defaultPadding().align(Alignment.CenterVertically)) {
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
                        color = Color(255, 255, 210),
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        if (!isFluid)
                            Text(
                                text = lang.get("simulation", "selector", "tooltip", "item"),
                                modifier = DefaultTheme.defaultPadding(),
                                color = MaterialTheme.colors.error
                            )
                    }
                },
                modifier = DefaultTheme.defaultPadding().align(Alignment.CenterVertically)) {
                    Button(
                        onClick = { updateIsFluid(true) },
                        enabled = isFluid.not(),
                        content = { Text(lang.get("simulation", "selector", "fluid")) }
                    )
            }
        }

        Row {
            OutlinedTextField(
                amount.toString(),
                modifier = DefaultTheme.defaultPadding().fillMaxWidth(0.6f).align(Alignment.CenterVertically),
                enabled = !isInfinite,
                onValueChange = {
                    updateAmount(if (it.trim().isEmpty()) 0 else try {
                        it.trim().toLong().coerceAtLeast(0)
                    } catch (e: Exception) {
                        amount
                    })
                },
                label = { Text(lang.get("simulation", "selector", "input-amount")) },
                colors = DefaultTheme.defaultTextField()
            )

            Checkbox(
                checked = isInfinite,
                onCheckedChange = { isInfinite = it },
                modifier = Modifier.padding(horizontal = 10.dp).align(Alignment.CenterVertically),
                colors = DefaultTheme.checkboxDefault()
            )

            Text(
                text = lang.get("simulation", "selector", "infinite"),
                modifier = Modifier.align(Alignment.CenterVertically),
                style = DefaultTheme.defaultTextStyle()
            )
        }

        Row {
            Button(
                modifier = DefaultTheme.defaultPadding().align(Alignment.CenterVertically),
                onClick = {
                    val hatch = if (matterVariant is FluidVariant == nuclearHatch.isFluid) nuclearHatch else NuclearHatch(matterVariant is FluidVariant)
                    if (matterVariant is FluidVariant != nuclearHatch.isFluid) {
                        hatch.inventory.outputCount = outputCount
                        for (i in 0 until outputCount) {
                            val holder = hatch.inventory.output[i]
                            holder.outputMaxAmount =
                                nuclearHatch.inventory.output[i].outputMaxAmount * if (isFluid) 1/81 else 81
                            holder.takeout = nuclearHatch.inventory.output[i].takeout * if (isFluid) 1/81 else 81
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

        Row {
            var text by remember { mutableStateOf("") }

            OutlinedTextField(
//                outputCount.toString()
                text
                ,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.padding(horizontal = 10.dp).align(Alignment.CenterVertically),
                label = { Text(lang.get("simulation", "selector", "output-count")) },
                onValueChange = {
                    println("text is :$it")
                    text = it
//                    outputCount = if (it.trim().isEmpty()) 0 else try {
//                        it.trim().toInt().coerceAtLeast(0)
//                    } catch (e: Exception) {
//                        outputCount
//                    }
//                    outputCount =
//                    nuclearHatch.inventory.outputCount = outputCount
                },
                colors = DefaultTheme.defaultTextField()
            )
        }

        Row {
            Button(
                modifier = Modifier.align(Alignment.CenterVertically).padding(10.dp, 5.dp),
                onClick = {
                    nuclearHatch.inventory.input.setMatterVariant(false,if (isFluid) FluidVariant.blank() else ItemVariant.blank(), 0)
                    closeWindow()
                },
                content = { Text(lang.get("simulation", "selector", "empty-input")) }
            )

            Button(
                modifier = Modifier.align(Alignment.CenterVertically).padding(10.dp, 5.dp),
                onClick = {
                    for (holder in nuclearHatch.inventory.output)
                        holder.empty()
                    closeWindow()
                },
                content = { Text(lang.get("simulation", "selector", "empty-output")) }
            )
        }

        Box {
            val listState = rememberLazyListState()
            LazyColumn(state = listState) {
                items(items = nuclearHatch.inventory.output) {
                    Item(lang, it)
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = listState
                )
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
    alwaysOnTop = true,
    state = window.state,
) {

    val nuclearHatch = window.nuclearHatch
    var matterVariant by remember { mutableStateOf(nuclearHatch.inventory.input.matterVariant) }
    var amount by remember {mutableStateOf(if (nuclearHatch.isFluid) nuclearHatch.inventory.input.amount / 81 else nuclearHatch.inventory.input.amount)}
    var isFluid by remember { mutableStateOf(nuclearHatch.isFluid) }

    MaterialTheme(colors = if (isSystemInDarkTheme()) DefaultTheme.dark else DefaultTheme.default) {
        
        Row(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            Column(Modifier.fillMaxWidth(0.4f)) {
                SelectorOverview(window.lang, nuclearHatch, isFluid,matterVariant,amount, {isFluid = it}, {amount = it}, {window.updateNuclearHatch(it)}, {window.close()})
            }

            Column {
                simulationSelectorCellLayout(5, Modifier.fillMaxSize()) {
                    if (!isFluid)
                        for (item in Constant.ITEMS) {
                            key(item) {
                                SimulationCell(window.lang, item, matterVariant.matter == item) {
                                    matterVariant = it
                                    if (amount == 0L)
                                        amount = 1
                                }
                            }
                        }
                    else
                        for (fluid in Constant.FLUIDS) {
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
}

@Composable
fun Item(lang:Lang, output:OutputMatterHolder) {
    var takeout by remember { mutableStateOf(output.takeout) }
    var maxAmount by remember { mutableStateOf(output.outputMaxAmount) }
    Row{
        Text(text = lang.get("simulation", "selector", "max-count"),
            modifier = Modifier.padding(horizontal = 10.dp).align(Alignment.CenterVertically),
            color = Color.Black)
        OutlinedTextField(
            if (output.isFluid)
            (maxAmount / 81).toString() else maxAmount.toString(),
            modifier = Modifier.padding(horizontal = 10.dp).align(Alignment.CenterVertically).fillMaxWidth(0.3f),
            onValueChange = {
                maxAmount = if (it.trim().isEmpty()) 0 else try {
                    if (output.isFluid)
                        it.trim().toLong().coerceAtLeast(-1)  * 81
                    else
                        it.trim().toLong().coerceAtLeast(-1)
                } catch (e: Exception) {
                    maxAmount
                }
                output.outputMaxAmount = maxAmount
            },
            colors = DefaultTheme.defaultTextField()
        )
        Text(text = lang.get("simulation", "selector", "takeout"),
            modifier = Modifier.padding(horizontal = 10.dp).align(Alignment.CenterVertically),
            color = Color.Black)
        OutlinedTextField(
            if (output.isFluid)
                (takeout / 81).toString() else takeout.toString(),
            modifier = Modifier.padding(horizontal = 10.dp).align(Alignment.CenterVertically),
            onValueChange = {
                takeout = if (it.trim().isEmpty()) 0 else try {
                    if (output.isFluid)
                        it.trim().toLong().coerceAtLeast(0) * 81
                    else
                        it.trim().toLong().coerceAtLeast(0)
                } catch (e: Exception) {
                    takeout
                }
                output.takeout = takeout
            },
            colors = DefaultTheme.defaultTextField()
        )
    }
}


