package top.focess.mc.mi.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import top.focess.mc.mi.nuclear.NuclearSimulation
import top.focess.mc.mi.nuclear.mc.ItemVariant
import top.focess.mc.mi.nuclear.mi.MINuclearInventory
import top.focess.mc.mi.nuclear.mi.NuclearReactionType
import top.focess.mc.mi.ui.dialog.DialogState
import top.focess.mc.mi.ui.lang.Lang
import top.focess.mc.mi.ui.simulation.SimulationSelectorState
import top.focess.scheduler.Task
import top.focess.scheduler.ThreadPoolScheduler
import top.focess.util.yaml.YamlConfiguration
import java.io.File
import java.nio.file.Path
import java.time.Duration
import javax.swing.JOptionPane

class GlobalState(lang: Lang) {

    // selector window
    val selectorState by mutableStateOf(SimulationSelectorState())

    val saveDialog = DialogState<Path?>()
    val newDialog = DialogState<NuclearReactionType?>()
    val openDialog = DialogState<Path?>()

    var directory: String? = null

    var simulation: NuclearSimulation? by mutableStateOf(null)

    // should be false if anything changed
    var isSaved by mutableStateOf(true)
    var name by mutableStateOf(lang.get("unopened"))
    var file: File? by mutableStateOf(null)

    var isStart by mutableStateOf(false)
    val scheduler = ThreadPoolScheduler(1, false, "TickManager")
    var tickTask: Task? = null
    var updateTask: Task? = null

    val itemInventory by mutableStateOf(MINuclearInventory(false))
    val fluidInventory by mutableStateOf(MINuclearInventory(true))

    init {
        itemInventory.outputCount = 100
        fluidInventory.outputCount = 100
    }
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
        val yml = YamlConfiguration(null)
        yml.set("simulation", state.simulation!!)
        yml.save(state.file!!)
        state.isSaved = true
    }

    suspend fun new() {
        if (!state.isSaved)
            this.save()
        state.simulation = NuclearSimulation({ _: Int, _: Int ->
            ItemVariant.blank()
        }, state.newDialog.awaitResult() ?: return)
        state.file = null
        state.name = lang.get("unsaved")
        state.isSaved = false
        state.selectorState.windows.clear()
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
            state.selectorState.windows.clear()
        } catch (e: Exception) {
            e.printStackTrace()
            JOptionPane.showMessageDialog(null, e.message, lang.get("open-fail"), JOptionPane.ERROR_MESSAGE)
        }
    }

    fun start() {
        if (!state.isStart && state.simulation != null) {
            state.selectorState.windows.clear()
            state.tickTask = state.scheduler.runTimer(
                {
                    state.simulation!!.tick()
                    val nuclearGrid = state.simulation!!.nuclearGrid
                    for (i in 0 until nuclearGrid.sizeX)
                        for (j in 0 until nuclearGrid.sizeY)
                            if (nuclearGrid.getNuclearTile(i, j).isPresent)
                                for (holder in nuclearGrid.getNuclearTile(i, j).get().inventory.output) {
                                    val variant = holder.matterVariant
                                    val amount = holder.extractAmount(holder.takeout)
                                    if (holder.isFluid) {
                                        if (state.fluidInventory.output(variant, amount) != amount)
                                            throw IllegalStateException("Failed to output")
                                    } else if (state.itemInventory.output(variant, amount) != amount)
                                        throw IllegalStateException("Failed to output")
                                }
                    state.isSaved = false
                },
                Duration.ZERO,
                Duration.ofMillis(50)
            )
            state.updateTask = state.scheduler.runTimer({
                state.isStart = false
                state.isStart = true
            }, Duration.ZERO, Duration.ofSeconds(1))
            state.isStart = true
        }
    }

    fun stop() {
        if (state.tickTask != null && state.isStart) {
            state.tickTask!!.cancel()
            state.updateTask!!.cancel()
            state.isSaved = false
            state.isStart = false
            state.tickTask = null
            state.updateTask = null
        }
    }


}