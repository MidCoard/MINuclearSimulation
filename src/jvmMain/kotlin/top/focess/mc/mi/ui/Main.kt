package top.focess.mc.mi.ui// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
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

@Composable
fun SimulatorView(lang: Lang, globalState: GlobalState, globalAction: GlobalAction) {

    MaterialTheme(colors = if(isSystemInDarkTheme()) DefaultTheme.dark else DefaultTheme.default) {
        Row(Modifier.background(MaterialTheme.colors.background)) {
            Column(Modifier.fillMaxWidth(0.2f)) {
                GeneralPanel(lang, globalState.isStart, globalState.simulation, globalState.tickTask, globalAction)
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

fun init() {
    //Use class forname to init the Items and Fluids
    Class.forName("top.focess.mc.mi.nuclear.mc.Items")
    Class.forName("top.focess.mc.mi.nuclear.mi.MIItems")
    Class.forName("top.focess.mc.mi.nuclear.mc.Fluids")
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
    saveAndExit: () -> Unit
) =
    Window(
        onCloseRequest = {
            saveAndExit()
        },
        title = "${lang.get("title")} ${globalState.name} ${if (globalState.isSaved) "" else "*"}",
        icon = icon,
        state = state,
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
                Item(lang.get("menu-bar", "simulation", "new"), mnemonic = 'N') {
                    new()
                }
                Item(lang.get("menu-bar", "simulation", "open"), mnemonic = 'O') {
                    open()
                }
                Item(
                    lang.get("menu-bar", "simulation", "save"),
                    mnemonic = 'S',
                    enabled = !globalState.isSaved
                ) {
                    save()
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

    if (state.isMinimized)
        TrayView(icon, lang, state) { saveAndExit() }

    if (!state.isMinimized)
        WindowView(icon, lang, globalState, globalAction, state, updateLang, {new()}, {open()}, {save()}, {saveAndExit()})
}

@Preview
fun main() =
    application {
        // initialize Items and Fluids
        init()

        // store the logo pic
        val icon = painterResource("logo.png")

        // set the default language to Chinese
        var lang by remember { mutableStateOf(Lang.default) }

        // store the global state
        val globalState by remember { mutableStateOf(GlobalState(lang)) }

        // store the global action
        val globalAction by remember { mutableStateOf(GlobalAction(lang, globalState)) }

        // the default window state and maximized the window
        val state = rememberWindowState(placement = WindowPlacement.Maximized)

        // the coroutine scope
        val scope = rememberCoroutineScope()

        // nuclear selector windows
        for (window in globalState.selectorState.windows)
            key(window) {
                SimulationSelector(window)
            }

        Simulator(icon, lang, globalState, globalAction, state, scope) { lang = it }

    }
