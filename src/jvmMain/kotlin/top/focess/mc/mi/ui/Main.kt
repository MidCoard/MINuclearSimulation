// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import androidx.compose.ui.window.Tray
import top.focess.mc.mi.nuclear.NuclearSimulation
import kotlin.reflect.KProperty

@Composable
@Preview
fun Simulation() {

    var nuclearSimulation : NuclearSimulation? = null

    var text by remember { mutableStateOf("Hello, World!") }
    MaterialTheme () {
        Row {

            Button(onClick = {
                text = "Hello, Desktop!"
            }) {
                Text(text)
            }
        }
        Row {

            Button(onClick = {
                text = "Hello, Desktop!"
            }) {
                Text(text)
            }
        }
        Row {

            Button(onClick = {
                text = "Hello, Desktop!"
            }) {
                Text(text)
            }
        }
    }
}

fun main() =
    application {
        val icon = painterResource("logo.png")
        Tray(
            icon,
            menu = {
                Item("Quit", onClick = ::exitApplication)
            }
        )

        Window(
            icon,
            title = "MINuclear Simulation",
            onCloseRequest = ::exitApplication
        ) {
            Simulation()
        }
    }
