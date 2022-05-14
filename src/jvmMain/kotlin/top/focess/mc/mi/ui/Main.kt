// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import top.focess.mc.mi.nuclear.NuclearSimulation
import top.focess.mc.mi.ui.GeneralPanel
import top.focess.mc.mi.ui.ObserverPanel
import top.focess.mc.mi.ui.SimulationChamber
import top.focess.mc.mi.ui.lang.Lang


fun SimulationView(
    children: @Composable() () -> Unit
) {

}
@Composable
@Preview
fun Simulation() {

    var nuclearSimulation : NuclearSimulation? = null

    MaterialTheme () {
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
        val lang = mutableStateOf(Lang.default)
        Tray(
            icon,
            menu = {
                Item(lang.value.get("quit"), onClick = ::exitApplication)
            }
        )

        Window(
            onCloseRequest = ::exitApplication,
            title = "MINuclear Simulation",
        ) {
            Simulation()

            MenuBar {
                Menu(lang.value.get("menu-bar","file","name"), mnemonic = 'F') {
                    Item(lang.value.get("quit"), onClick = ::exitApplication)
                }
                Menu(lang.value.get("menu-bar","language","name"), mnemonic = 'L') {
                    Item(lang.value.get("menu-bar","language","english"), onClick = {lang.value = Lang.en_US})
                    Item(lang.value.get("menu-bar","language","chinese"), onClick = {lang.value = Lang.zh_CN})
                }
            }
        }
    }
