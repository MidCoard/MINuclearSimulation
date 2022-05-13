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
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Tray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    Image(
        painter = painterResource("icon.jpeg"),
        modifier = Modifier.fillMaxSize(),
        contentDescription = "Sample image"
    )
    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() =
    application {

        Tray(
            icon = painterResource("icon.jpeg"),
            menu = {
                Item("Quit", onClick = ::exitApplication)
            }
        )

        Window(
            icon = painterResource("icon.jpeg"),
            title = "MINuclear Simulation",
            onCloseRequest = ::exitApplication
        ) {
            App()
        }
    }
