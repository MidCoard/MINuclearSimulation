package top.focess.mc.mi.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import aztech.modern_industrialization.machines.blockentities.hatches.NuclearHatch
import top.focess.mc.mi.nuclear.NuclearSimulation
import top.focess.mc.mi.ui.lang.Lang

@Composable
fun SimulationChamber(lang: Lang, simulation: NuclearSimulation?) {
    if (simulation != null)
        nuclearSimulationView(simulation)
    else
        emptyView(lang)

}

@Composable
fun nuclearSimulationView(simulation: NuclearSimulation) {

    Box(modifier = Modifier.fillMaxSize().background(lightColors().onError)) {
        repeat(simulation.nuclearType.size) {x :Int ->
            if (simulation.nuclearType.row.test(x))
                LazyRow (modifier = Modifier.fillMaxSize().fillMaxWidth(100f).align(Alignment.Center)) {
                    println(x)
                    items(simulation.nuclearType.size) { y :Int ->
                        if (simulation.nuclearGrid.getNuclearTile(x,y).isPresent) {
                            println("$x $y")
                            nuclearSimulationCell(simulation.nuclearGrid.getNuclearTile(x, y).get() as NuclearHatch)
                        }
                    }
                }
        }
    }

}

@Composable
fun emptyView(lang: Lang) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.align(Alignment.Center)) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = LocalContentColor.current.copy(alpha = 0.60f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                lang.get("simulation","no-simulation"),
                color = LocalContentColor.current.copy(alpha = 0.60f),
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(16.dp)
            )
        }
    }
}

@Composable
fun nuclearSimulationCell(nuclearHatch: NuclearHatch) {
    Box (
        modifier = Modifier.fillMaxSize().background(lightColors().background).border(1.dp, LocalContentColor.current.copy(alpha = 0.60f))
            ) {
        Text("hello ?")

    }
}
