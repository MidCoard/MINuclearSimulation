package top.focess.mc.mi.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.focess.mc.mi.nuclear.mi.NuclearReactionType
import top.focess.mc.mi.ui.lang.Lang
import java.awt.FileDialog
import java.io.File
import java.nio.file.Path
import javax.swing.JOptionPane

@Composable
fun FrameWindowScope.FileDialog(
    lang: Lang,
    isLoad: Boolean,
    file: String? = null,
    directory: String? = null,
    updateDirectory: (String) -> Unit = {},
    state: DialogState<Path?>
) = AwtWindow(
    create = {
        object : FileDialog(window, lang.get("dialog", "file", "choose-file"), if (isLoad) LOAD else SAVE) {

            init {
                this.file = file
                this.directory = directory ?: System.getProperty("user.home")
            }

            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    if (this.directory != null && this.file != null) {
                        updateDirectory(this.directory)
                        state.onResult(File(this.directory).resolve(this.file).toPath())
                    } else state.onResult(null)
                }
            }
        }
    },
    dispose = FileDialog::dispose
)

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun WindowScope.NuclearTypeDialog(
    lang: Lang,
    state: DialogState<NuclearReactionType?>
) {
    DisposableEffect(Unit) {
        val job = GlobalScope.launch(Dispatchers.IO) {
            val result = JOptionPane.showInputDialog(
                window,
                lang.get("dialog", "simulation", "name"),
                lang.get("dialog", "simulation", "title"),
                JOptionPane.INFORMATION_MESSAGE,
                null,
                NuclearReactionType.values(),
                NuclearReactionType.SIMULATION_3X3
            )
            state.onResult(result as NuclearReactionType?)
        }

        onDispose {
            job.cancel()
        }
    }
}