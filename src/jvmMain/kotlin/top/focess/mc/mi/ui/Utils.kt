package top.focess.mc.mi.ui

import aztech.modern_industrialization.machines.components.NeutronHistoryComponent
import aztech.modern_industrialization.machines.components.TemperatureComponent
import aztech.modern_industrialization.nuclear.NeutronType
import top.focess.mc.mi.nuclear.mc.MatterHolder
import top.focess.mc.mi.nuclear.mc.MatterVariant
import top.focess.mc.mi.ui.lang.Lang

fun String.substring0(end: Int) = if (this.length < end) this.substring(0) else this.substring(0, end)

fun showTag(tag: Map<String, Any>): String {
    if (tag.size == 1 && tag.containsKey("desRem"))
        return "(" + tag["desRem"].toString() + ")"
    else if (tag.isEmpty())
        return ""
    return tag.toString()
}

fun showAmount(holder: MatterHolder): String =
    if (!holder.isFluid)
        holder.amount.toString()
    else ((holder.amount / 81000).toString() + "B(" + holder.amount % 81000 / 81 + "mb[" + holder.amount % 81000 % 81 + "/81])")

fun showName(lang: Lang, matterVariant: MatterVariant, tag: Map<String, Any>): String =
    lang.get("matter", matterVariant.matter!!.namespace, matterVariant.matter!!.name) + showTag(tag)

fun showTemperature(lang: Lang, temperatureComponent: TemperatureComponent) = lang.get("simulation", "temperature") + ": " + temperatureComponent.temperature.toString().substring0(6) + "/" + temperatureComponent.temperatureMax.toString()

fun showNeutronGeneration(lang: Lang, neutronHistory: NeutronHistoryComponent) = lang.get("simulation", "neutron", "generation") + ": " + neutronHistory.averageGeneration.toString().substring0(6)

fun showAvgEUGeneration(lang: Lang, neutronHistory: NeutronHistoryComponent) =
    lang.get("simulation", "neutron", "eu-generation") + ": " + neutronHistory.averageEuGeneration.toString().substring0(6)


fun showNeutronReceive(lang: Lang, neutronHistory: NeutronHistoryComponent) = lang.get("simulation", "neutron", "receive") + ": " + neutronHistory.getAverageReceived(NeutronType.BOTH).toString().substring0(6)+
        "(" + lang.get("simulation", "neutron", "fast") + ": " + neutronHistory.getAverageReceived(NeutronType.FAST).toString().substring0(6)+ "," +
        lang.get("simulation", "neutron", "thermal") + ": " + neutronHistory.getAverageReceived(NeutronType.THERMAL).toString().substring0(6) + ")"

fun showNeutronFlux(lang: Lang, neutronHistory: NeutronHistoryComponent) = lang.get("simulation", "neutron", "flux") + ": " + neutronHistory.getAverageFlux(NeutronType.BOTH).toString().substring0(6) +
        "(" + lang.get("simulation", "neutron", "fast") + ": " + neutronHistory.getAverageFlux(NeutronType.FAST).toString().substring0(6) + "," +
        lang.get("simulation", "neutron", "thermal") + ": " + neutronHistory.getAverageFlux(NeutronType.THERMAL).toString().substring0(6) + ")"