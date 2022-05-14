package top.focess.mc.mi.ui// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.*
import kotlinx.coroutines.launch
import top.focess.mc.mi.ui.lang.Lang


@Composable
@Preview
fun Simulation() {


    MaterialTheme() {
        GeneralPanel() {

        }
        SimulationChamber() {

        }
        ObserverPanel() {

        }
    }
}

fun main() =
    application {
        val icon = painterResource("logo.png")
        var lang by remember { mutableStateOf(Lang.default) }
        val state = rememberWindowState()
        val scope = rememberCoroutineScope()

        val globalState = remember { GlobalState(lang) }
        val globalAction = GlobalAction(lang, globalState)

        Tray(
            icon,
            menu = {
                Item(lang.get("tray","maximize")) {
                    state.isMinimized = false
                    state.placement = WindowPlacement.Maximized
                }
                Item(lang.get("quit"), onClick = ::exitApplication)
            }
        )

        Window(
            onCloseRequest = ::exitApplication,
            title = "${lang.get("title")} ${globalState.name} ${if (globalState.isSaved) "" else "*"}",
            icon = icon,
        ) {
            Simulation()

            if (globalState.saveDialog.isAwaiting) {
                FileDialog(
                    lang,
                    false,
                    "untitled.mins",
                    globalState,
                    globalState.saveDialog
                )
            }

            if (globalState.openDialog.isAwaiting) {
                FileDialog(
                    lang,
                    true,
                    "",
                    globalState,
                    globalState.openDialog
                )
            }

            if (globalState.newDialog.isAwaiting) {
                NuclearTypeDialog(
                    lang,
                    globalState.newDialog
                )
            }

            fun new() = scope.launch { globalAction.new() }
            fun save() = scope.launch { globalAction.save() }
            fun open() = scope.launch { globalAction.open() }

            MenuBar {
                Menu(lang.get("menu-bar", "simulation", "name"), mnemonic = 'S') {
                    Item(lang.get("menu-bar", "simulation", "new"), mnemonic = 'N') {
                        new()
                    }
                    Item(lang.get("menu-bar", "simulation", "open"), mnemonic = 'O') {
                        open()
                    }
                    Item(lang.get("menu-bar", "simulation", "save"), mnemonic = 'S') {
                        save()
                    }
                    Item(lang.get("quit"), mnemonic = 'Q', onClick = ::exitApplication)
                }
                Menu(lang.get("menu-bar", "language", "name"), mnemonic = 'L') {
                    Item(lang.get("menu-bar", "language", "chinese"), onClick = { lang = Lang.zh_CN })
                    Item(lang.get("menu-bar", "language", "english"), onClick = { lang = Lang.en_US })
                }
            }


        }
    }
