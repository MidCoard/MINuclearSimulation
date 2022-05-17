package top.focess.mc.mi.ui// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.launch
import top.focess.mc.mi.ui.lang.Lang
import top.focess.mc.mi.ui.panel.GeneralPanel
import top.focess.mc.mi.ui.panel.ObserverPanel
import top.focess.mc.mi.ui.simulation.SimulationChamber
import top.focess.mc.mi.ui.simulation.simulationSelector
import top.focess.mc.mi.ui.theme.DefaultTheme

@Composable
fun simulatorLayout(
    modifier: Modifier,
    how: Placeable.PlacementScope.(generalPanel: Measurable, simulationChamber: Measurable, observerPanel: Measurable, constraints: Constraints) -> Unit,
    children: @Composable () -> Unit
) = Layout({ children() }, modifier) { measurables, constraints ->
    require(measurables.size == 3)
    layout(constraints.maxWidth, constraints.maxHeight) {
        how(measurables[0], measurables[1], measurables[2], constraints)
    }
}

@Composable
fun Simulator(lang: Lang, globalState: GlobalState, globalAction: GlobalAction) {

    MaterialTheme(colors = DefaultTheme.default) {
        simulatorLayout(Modifier.fillMaxSize(), { generalPanel, simulationChamber, observerPanel, constraints ->
            val generalWidth = constraints.maxWidth / 6
            val observerWidth = constraints.maxWidth / 6
            val simulationChamberWidth = constraints.maxWidth - generalWidth - observerWidth
            generalPanel.measure(
                Constraints(
                    maxWidth = generalWidth,
                    maxHeight = constraints.maxHeight,
                    minWidth = generalWidth,
                    minHeight = constraints.maxHeight
                )
            ).place(0, 0)

            simulationChamber.measure(
                Constraints(
                    maxWidth = simulationChamberWidth,
                    maxHeight = constraints.maxHeight,
                    minWidth = simulationChamberWidth,
                    minHeight = constraints.maxHeight
                )
            ).place(generalWidth, 0)

            observerPanel.measure(
                Constraints(
                    maxWidth = observerWidth,
                    maxHeight = constraints.maxHeight,
                    minWidth = observerWidth,
                    minHeight = constraints.maxHeight
                )
            ).place(generalWidth + simulationChamberWidth, 0)

        }) {
            GeneralPanel(lang, globalState.isStart, globalState.simulation, globalState.tickTask, globalAction)
            SimulationChamber(lang, globalState.isStart, globalState.selectorState, globalState.simulation) { x, y, it ->
                if (it != null)
                    globalState.simulation!!.nuclearGrid.setNuclearTile(x, y, it)
            }
            ObserverPanel(lang, globalState.simulation)
        }
    }
}

@Preview
fun main() =
    application {

        Class.forName("top.focess.mc.mi.nuclear.mi.MIItems")
        val icon = painterResource("logo.png")
        var lang by remember { mutableStateOf(Lang.default) }
        val globalState by remember { mutableStateOf(GlobalState(lang)) }
        val globalAction by remember { mutableStateOf(GlobalAction(lang, globalState)) }

        val state = rememberWindowState(width = Dp.Unspecified, height = Dp.Unspecified)
        val scope = rememberCoroutineScope()

        for (window in globalState.selectorState.windows)
            key(window) {
                simulationSelector(window)
            }

        fun new() = scope.launch { globalAction.new() }
        fun save() = scope.launch { globalAction.save() }
        fun saveAndExit() = scope.launch {
            globalAction.save()
            exitApplication()
        }
        fun open() = scope.launch { globalAction.open() }

        if (state.isMinimized)
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

        if (!state.isMinimized)
            Window(
                onCloseRequest = {
                    saveAndExit()
                },
                title = "${lang.get("title")} ${globalState.name} ${if (globalState.isSaved) "" else "*"}",
                icon = icon,
                state = state
            ) {
                Simulator(lang, globalState, globalAction)

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
                        Item(lang.get("menu-bar", "language", "chinese"), onClick = { lang = Lang.zh_CN })
                        Item(lang.get("menu-bar", "language", "english"), onClick = { lang = Lang.en_US })
                    }
                }
            }
    }
