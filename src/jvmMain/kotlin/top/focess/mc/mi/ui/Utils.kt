package top.focess.mc.mi.ui

import aztech.modern_industrialization.machines.components.NeutronHistoryComponent
import aztech.modern_industrialization.machines.components.TemperatureComponent
import aztech.modern_industrialization.nuclear.NeutronType
import aztech.modern_industrialization.nuclear.NuclearComponentItem
import top.focess.mc.mi.nuclear.mc.InputMatterHolder
import top.focess.mc.mi.nuclear.mc.MatterHolder
import top.focess.mc.mi.nuclear.mc.MatterVariant
import top.focess.mc.mi.nuclear.mc.OutputMatterHolder
import top.focess.mc.mi.ui.lang.Lang

fun String.substring0(end: Int) = if (this.length < end) this.substring(0) else this.substring(0, end)

fun showTag(tag: Map<String, Any>): String {
    if (tag.size == 1 && tag.containsKey("desRem"))
        return "(" + tag["desRem"].toString() + ")"
    else if (tag.isEmpty())
        return ""
    return tag.toString()
}

fun showAmount(lang: Lang, holder: MatterHolder): String =
    if (holder is InputMatterHolder && holder.isInfinite)
        lang.get("simulation","infinite")
    else if (!holder.isFluid)
        holder.amount.toString()
    else ((holder.amount / 81000).toString() + "B(" + holder.amount % 81000 / 81 + "mb[" + holder.amount % 81000 % 81 + "/81])")

fun showName(lang: Lang, matterVariant: MatterVariant, tag: Map<String, Any>): String =
    if (!matterVariant.isBlank) lang.get("matter", matterVariant.matter!!.namespace, matterVariant.matter!!.name) + showTag(tag) else lang.get("matter","empty")

fun showTemperature(lang: Lang, holder: InputMatterHolder, temperatureComponent: TemperatureComponent): String {
    val matterVariant = holder.matterVariant
    var maxTemperature = temperatureComponent.temperatureMax
    if (!matterVariant.isBlank && matterVariant.matter is NuclearComponentItem)
        maxTemperature = (matterVariant.matter as NuclearComponentItem).maxTemperature.toDouble()
    return lang.get("simulation", "temperature") + ": " + temperatureComponent.temperature.toString().substring0(6) + "/" + maxTemperature
}

fun showNeutronGeneration(lang: Lang, neutronHistory: NeutronHistoryComponent) = lang.get("simulation", "neutron", "generation") + ": " + neutronHistory.averageGeneration.toString().substring0(6)

fun showAvgEUGeneration(lang: Lang, neutronHistory: NeutronHistoryComponent) =
    lang.get("simulation", "neutron", "eu-generation") + ": " + neutronHistory.averageEuGeneration.toString().substring0(6)


fun showNeutronReceive(lang: Lang, neutronHistory: NeutronHistoryComponent) = lang.get("simulation", "neutron", "receive") + ": " + neutronHistory.getAverageReceived(NeutronType.BOTH).toString().substring0(6)+
        "(" + lang.get("simulation", "neutron", "fast") + ": " + neutronHistory.getAverageReceived(NeutronType.FAST).toString().substring0(6)+ "," +
        lang.get("simulation", "neutron", "thermal") + ": " + neutronHistory.getAverageReceived(NeutronType.THERMAL).toString().substring0(6) + ")"

fun showNeutronFlux(lang: Lang, neutronHistory: NeutronHistoryComponent) = lang.get("simulation", "neutron", "flux") + ": " + neutronHistory.getAverageFlux(NeutronType.BOTH).toString().substring0(6) +
        "(" + lang.get("simulation", "neutron", "fast") + ": " + neutronHistory.getAverageFlux(NeutronType.FAST).toString().substring0(6) + "," +
        lang.get("simulation", "neutron", "thermal") + ": " + neutronHistory.getAverageFlux(NeutronType.THERMAL).toString().substring0(6) + ")"

fun showOutput(lang: Lang, output: List<OutputMatterHolder>) :String {
    var result = ""
    for (outputHolder in output) {
        val matterVariant = outputHolder.matterVariant
        if (!matterVariant.isBlank) {
            result += showName(lang, matterVariant, outputHolder.tag) + ": " + showAmount(
                lang,
                outputHolder
            ) + "\n"
        }
    }
    return result
}