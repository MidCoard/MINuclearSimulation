package top.focess.mc.mi.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.Constraints
import top.focess.mc.mi.nuclear.mc.InputMatterHolder
import top.focess.mc.mi.nuclear.mc.MatterHolder
import top.focess.mc.mi.nuclear.mc.MatterVariant
import top.focess.mc.mi.nuclear.mc.OutputMatterHolder
import top.focess.mc.mi.nuclear.mi.Texture
import top.focess.mc.mi.ui.lang.Lang
import top.focess.mc.mi.ui.showAmount
import top.focess.mc.mi.ui.showName
import top.focess.mc.mi.ui.theme.DefaultTheme


@Composable
fun inventoryViewLayout(
    countPerRow: Int,
    modifier: Modifier,
    children: @Composable () -> Unit
) = Layout({ children() }, modifier) {
        measurables, constraints ->
    layout(constraints.maxWidth, constraints.maxHeight) {
        val rowCount = if (measurables.size % countPerRow == 0) measurables.size / countPerRow else measurables.size / countPerRow + 1
        for (i in 0 until rowCount) {
            for (j in 0 until countPerRow) {
                val index = i * countPerRow + j
                val nowRowViewCount = if (i == rowCount - 1) measurables.size - i * countPerRow else countPerRow
                if (index < measurables.size) {
                    measurables[index].measure(
                        Constraints(
                            minWidth = constraints.maxWidth / nowRowViewCount,
                            maxWidth = constraints.maxWidth / nowRowViewCount,
                            minHeight = (constraints.maxWidth / nowRowViewCount).coerceAtMost(constraints.maxHeight / rowCount),
                            maxHeight = (constraints.maxWidth / nowRowViewCount).coerceAtMost(constraints.maxHeight / rowCount)
                        )
                    ).place(
                        j * constraints.maxWidth / nowRowViewCount,
                        i * (constraints.maxWidth / nowRowViewCount).coerceAtMost(constraints.maxHeight / rowCount)
                    )
                } else break
            }
        }
    }
}


@Composable
fun MatterView(lang: Lang, holder: MatterHolder, matterVariant: MatterVariant, tag: Map<String,Any>)  {
    val texture = Texture.get(matterVariant.matter!!)
    Text(
        showName(lang, matterVariant, tag) + " - " + showAmount(lang, holder),
        style = DefaultTheme.midSmallTextStyle(),
        modifier = DefaultTheme.defaultPadding()
    )
    Box {
        Image(
            bitmap = loadImageBitmap(texture.inputStream),
            lang.get("simulation", "output"),
            modifier = Modifier.fillMaxSize(),
        )
    }
}


@Composable
fun OutputView(lang: Lang, holders: List<OutputMatterHolder>) {
    for (holder in holders) {
        val matterVariant = holder.matterVariant
        if (!matterVariant.isBlank && holder.amount != 0L)
            Column (Modifier.fillMaxSize()) {
                MatterView(lang, holder, matterVariant, holder.tag)
            }
    }
}

@Composable
fun InputView(lang: Lang, holder: InputMatterHolder) {
    val matterVariant = holder.matterVariant
    Column(Modifier.fillMaxSize()) {
        if (!matterVariant.isBlank && holder.amount != 0L) {
            MatterView(lang, holder, matterVariant, holder.tag)
        } else {
            Text(
                modifier = DefaultTheme.defaultPadding(),
                text = lang.get("simulation", "empty"),
                style = DefaultTheme.midSmallTextStyle()
            )
        }
    }
}