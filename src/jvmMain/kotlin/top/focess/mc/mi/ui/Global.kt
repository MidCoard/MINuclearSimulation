package top.focess.mc.mi.ui

import androidx.compose.runtime.*
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowScope
import kotlinx.coroutines.*
import top.focess.mc.mi.nuclear.NuclearSimulation
import top.focess.mc.mi.nuclear.mc.ItemVariant
import top.focess.mc.mi.nuclear.mi.NuclearReactionType
import top.focess.mc.mi.ui.lang.Lang
import top.focess.mc.mi.ui.simulation.SimulationSelectorState
import top.focess.scheduler.Task
import top.focess.scheduler.ThreadPoolScheduler
import top.focess.util.yaml.YamlConfiguration
import java.awt.FileDialog
import java.io.File
import java.nio.file.Path
import java.time.Duration
import javax.swing.JOptionPane

class GlobalState(lang: Lang) {

    // selector window
    val selectors by mutableStateOf(SimulationSelectorState())

    val saveDialog = DialogState<Path?>()
    val newDialog = DialogState<NuclearReactionType?>()
    val openDialog = DialogState<Path?>()

    var directory: String? = null

    var simulation : NuclearSimulation? by  mutableStateOf(null)
    // should be false if anything changed
    var isSaved by mutableStateOf(true)
    var name by mutableStateOf(lang.get("unopened"))
    var file :File? by mutableStateOf(null)

    var isStart by mutableStateOf(false)
    val scheduler = ThreadPoolScheduler(1,false,"TickManager")
    var tickTask : Task? = null
}

class GlobalAction(private val lang: Lang, private val state: GlobalState) {

    suspend fun save() {
        if (state.isSaved)
            return
        if (state.file == null) {
            val path = state.saveDialog.awaitResult() ?: return
            state.file = path.toFile()
            state.name = path.fileName.toString()
        }
        val yml = YamlConfiguration(null);
        yml.set("simulation", state.simulation!!)
        yml.save(state.file!!)
        state.isSaved = true
    }

    suspend fun new() {
        if (!state.isSaved)
            this.save()
        state.simulation = NuclearSimulation({ _: Int, _: Int ->
            ItemVariant.blank()
        }, state.newDialog.awaitResult() ?: return);
        state.file = null
        state.name = lang.get("unsaved")
        state.isSaved = false
        state.selectors.windows.clear()
    }

    suspend fun open() {
        if (!state.isSaved)
            this.save()
        val path = state.openDialog.awaitResult() ?: return
        try {
            val yml = YamlConfiguration.loadFile(path.toFile())
            state.simulation = yml.get("simulation")
            state.file = path.toFile()
            state.name = path.fileName.toString()
            state.isSaved = true
            state.selectors.windows.clear()
        } catch (e:Exception) {
            JOptionPane.showMessageDialog(null, e.message,lang.get("open-fail"), JOptionPane.ERROR_MESSAGE)
        }
    }

    fun start() {
        if (!state.isStart && state.simulation != null) {
            state.selectors.windows.clear()
            state.tickTask = state.scheduler.runTimer(
                {
                    state.simulation!!::tick
                    state.isSaved = false
                },
                Duration.ZERO,
                Duration.ofMillis(50)
            )
            state.isStart = true
        }
    }

    fun stop() {
        if (state.tickTask != null && state.isStart) {
            state.tickTask!!.cancel()
            state.isSaved = false
            state.isStart = false
            state.tickTask = null
        }
    }


}

class DialogState<T> {
    private var onResult: CompletableDeferred<T>? by mutableStateOf(null)

    val isAwaiting get() = onResult != null

    suspend fun awaitResult(): T {
        onResult = CompletableDeferred()
        val result = onResult!!.await()
        onResult = null
        return result
    }

    fun onResult(result: T) = onResult!!.complete(result)
}

@Composable
fun FrameWindowScope.FileDialog(
    lang: Lang,
    isLoad: Boolean,
    file: String? = null,
    globalState: GlobalState,
    state: DialogState<Path?>
) = AwtWindow(
    create = {
        object : FileDialog(window,lang.get("dialog","file","choose-file") , if (isLoad) LOAD else SAVE) {

            init {
                this.file = file
                this.directory = globalState.directory ?: System.getProperty("user.home")
            }

            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    if (directory != null && file != null) {
                        globalState.directory = directory
                        state.onResult(File(directory).resolve(this.file).toPath())
                    }
                    else state.onResult(null)
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
                window, lang.get("dialog","simulation","name"), lang.get("dialog","simulation","title"), JOptionPane.INFORMATION_MESSAGE,null,NuclearReactionType.values(),NuclearReactionType.SIMULATION_3X3
            )
            state.onResult(result as NuclearReactionType?)
        }

        onDispose {
            job.cancel()
        }
    }
}