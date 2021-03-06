package top.focess.mc.mi.ui// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import top.focess.mc.mi.ui.dialog.FileDialog
import top.focess.mc.mi.ui.dialog.NuclearTypeDialog
import top.focess.mc.mi.ui.lang.Lang
import top.focess.mc.mi.ui.panel.GeneralPanel
import top.focess.mc.mi.ui.panel.ObserverPanel
import top.focess.mc.mi.ui.simulation.SimulationChamber
import top.focess.mc.mi.ui.simulation.SimulationSelector
import top.focess.mc.mi.ui.theme.DefaultTheme


var gState: GlobalState? = null

@Composable
fun SimulatorView(lang: Lang, globalState: GlobalState, globalAction: GlobalAction) {

    MaterialTheme(colors = DefaultTheme.getDefault()) {
        Row(Modifier.background(MaterialTheme.colors.background)) {
            Column(Modifier.fillMaxWidth(0.2f)) {
                GeneralPanel(lang, globalState.isStart, globalState.isInfinite, globalState.simulation, globalState.tickRate, globalAction, { globalState.tickRate = it },{globalState.isInfinite = it})
            }
            // next width is divided by the rest of the width
            Column(Modifier.fillMaxWidth(0.75f)) {
                SimulationChamber(
                    lang,
                    globalState.isStart,
                    globalState.selectorState,
                    globalState.simulation
                ) { x, y, it ->
                    if (it != null)
                        globalState.simulation!!.nuclearGrid.setNuclearTile(x, y, it)
                }
            }
            Column {
                ObserverPanel(lang, globalState.simulation, globalState.itemInventory, globalState.fluidInventory)
            }
        }
    }
}

fun init(globalState: GlobalState) {
    //Use class forname to init the Items and Fluids
    Class.forName("top.focess.mc.mi.nuclear.mc.Items")
    Class.forName("top.focess.mc.mi.nuclear.mi.MIItems")
    Class.forName("top.focess.mc.mi.nuclear.mc.Fluids")
    gState = globalState
}

@Composable
fun ApplicationScope.TrayView(
    icon: Painter,
    lang: Lang,
    state: WindowState,
    saveAndExit: () -> Unit
) =
    Tray(
        icon,
        menu = {
            Item(lang.get("tray", "maximize"), onClick = {
                state.isMinimized = false
                state.placement = WindowPlacement.Floating
            })
            Item(lang.get("quit"), onClick = {
                saveAndExit()
            })
        }
    )

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WindowView(
    icon: Painter,
    lang: Lang,
    globalState: GlobalState,
    globalAction: GlobalAction,
    state: WindowState,
    updateLang: (Lang) -> Unit,
    new: () -> Unit,
    open: () -> Unit,
    save: () -> Unit,
    saveAs: () -> Unit,
    saveAndExit: () -> Unit
) =
    Window(
        onCloseRequest = {
            saveAndExit()
        },
        title = "${lang.get("title")} ${globalState.name} ${if (globalState.isSaved) "" else "*"}",
        icon = icon,
        state = state,
        focusable = true
    ) {

        SimulatorView(lang, globalState, globalAction)

        if (globalState.saveDialog.isAwaiting) {
            FileDialog(
                lang,
                false,
                "untitled.mins",
                globalState.directory,
                { globalState.directory = it },
                globalState.saveDialog
            )
        }

        if (globalState.openDialog.isAwaiting) {
            FileDialog(
                lang,
                true,
                "",
                globalState.directory,
                { globalState.directory = it },
                globalState.openDialog
            )
        }

        if (globalState.newDialog.isAwaiting) {
            NuclearTypeDialog(
                lang,
                globalState.newDialog
            )
        }


        MenuBar {
            Menu(lang.get("menu-bar", "simulation", "name"), mnemonic = 'S') {
                Item(lang.get("menu-bar", "simulation", "new"), mnemonic = 'N',
                shortcut = KeyShortcut(Key.N, ctrl = true)) {
                    new()
                }
                Item(lang.get("menu-bar", "simulation", "open"), mnemonic = 'O') {
                    open()
                }
                Item(
                    lang.get("menu-bar", "simulation", "save"),
                    mnemonic = 'S',
                    enabled = !globalState.isSaved,
                    shortcut = KeyShortcut(Key.S, ctrl = true)
                ) {
                    save()
                }
                Item(
                    lang.get("menu-bar", "simulation", "save-as"),
                    mnemonic = 'A',
                    enabled = globalState.simulation != null
                ) {
                    saveAs()
                }
                Item(
                    lang.get("menu-bar", "simulation", "start"),
                    mnemonic = 'T',
                    enabled = !globalState.isStart && globalState.simulation != null
                ) {
                    globalAction.start()
                }
                Item(
                    lang.get("menu-bar", "simulation", "stop"),
                    mnemonic = 'P',
                    enabled = globalState.isStart && globalState.tickTask != null
                ) {
                    globalAction.stop()
                }
                Item(lang.get("quit"), mnemonic = 'Q', onClick = {
                    saveAndExit()
                })
            }
            Menu(lang.get("menu-bar","theme","name"), mnemonic = 'T') {
                Item(lang.get("menu-bar","theme","dark"), mnemonic = 'D',
                    enabled = !DefaultTheme.isThemeDark(),
                    onClick = {
                    DefaultTheme.setDefaultColor(DefaultTheme.dark)
                })
                Item(lang.get("menu-bar","theme","light"), mnemonic = 'L',
                    enabled = DefaultTheme.isThemeDark(),
                    onClick = {
                    DefaultTheme.setDefaultColor(DefaultTheme.light)
                })
            }

            Menu(lang.get("menu-bar", "language", "name"), mnemonic = 'L') {
                Item(lang.get("menu-bar", "language", "chinese"), enabled = lang != Lang.zh_CN){ updateLang(Lang.zh_CN) }
                Item(lang.get("menu-bar", "language", "english"), enabled = lang != Lang.en_US){ updateLang(Lang.en_US) }
            }
        }
    }

@Composable
fun ApplicationScope.Simulator(
    icon: Painter,
    lang: Lang,
    globalState: GlobalState,
    globalAction: GlobalAction,
    state: WindowState,
    scope: CoroutineScope,
    updateLang: (Lang) -> Unit,
) {

    fun new() = scope.launch { globalAction.new() }
    fun save() = scope.launch { globalAction.save() }
    fun saveAndExit() = scope.launch {
        globalAction.save()
        exitApplication()
    }
    fun open() = scope.launch { globalAction.open() }
    fun saveAs() = scope.launch { globalAction.saveAs() }

    if (state.isMinimized)
        TrayView(icon, lang, state) { saveAndExit() }

    if (!state.isMinimized)
        WindowView(icon, lang, globalState, globalAction, state, updateLang, {new()}, {open()}, {save()},{saveAs()}, {saveAndExit()})
}

@Preview
fun main() {
    Thread {
        while (true) {
            try {
                if (gState != null) {
                    val gState0 = gState!!
                    if (gState0.isInfinite && gState0.isStart) {
                        gState0.simulation!!.tick()
                        val nuclearGrid = gState0.simulation!!.nuclearGrid
                        for (i in 0 until nuclearGrid.sizeX)
                            for (j in 0 until nuclearGrid.sizeY)
                                if (nuclearGrid.getNuclearTile(i, j).isPresent)
                                    for (holder in nuclearGrid.getNuclearTile(i, j).get().inventory.output) {
                                        val variant = holder.matterVariant
                                        val amount = holder.extractAmount(holder.takeout)
                                        if (holder.isFluid) {
                                            if (gState0.fluidInventory.output(variant, amount) != amount)
                                                throw IllegalStateException("Failed to output")
                                        } else if (gState0.itemInventory.output(variant, amount) != amount)
                                            throw IllegalStateException("Failed to output")
                                    }
                        gState0.isSaved = false
                    }
                }
                Thread.sleep(0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }.start()
    
    application {

        // store the logo pic
        val icon = painterResource("logo.png")

        // set the default language to Chinese
        var lang by remember { mutableStateOf(Lang.default) }

        // store the global state
        val globalState = remember { GlobalState(lang) }

        // store the global action
        val globalAction = remember { GlobalAction(lang, globalState) }

        // the default window state and maximized the window
        val state = rememberWindowState(placement = WindowPlacement.Maximized)

        // the coroutine scope
        val scope = rememberCoroutineScope()

        // nuclear selector windows
        for (window in globalState.selectorState.windows)
            key(window) {
                SimulationSelector(window)
            }

        // initialize Items and Fluids
        LaunchedEffect(Unit) {
            init(globalState)
        }

        Simulator(icon, lang, globalState, globalAction, state, scope) { lang = it }
    }
}
