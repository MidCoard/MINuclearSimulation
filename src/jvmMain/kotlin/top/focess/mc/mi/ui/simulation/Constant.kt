package top.focess.mc.mi.ui.simulation

import okhttp3.internal.immutableListOf
import top.focess.mc.mi.nuclear.mc.Fluids
import top.focess.mc.mi.nuclear.mc.Items
import top.focess.mc.mi.nuclear.mi.MIItems


val ITEMS = immutableListOf(
    MIItems.U_FUEL_ROD, MIItems.U_FUEL_ROD_DOUBLE, MIItems.U_FUEL_ROD_QUAD, MIItems.U_FUEL_ROD_DEPLETED,
    MIItems.HEU_FUEL_ROD, MIItems.HEU_FUEL_ROD_DOUBLE, MIItems.HEU_FUEL_ROD_QUAD, MIItems.HEU_FUEL_ROD_DEPLETED,
    MIItems.HE_MOX_FUEL_ROD, MIItems.HE_MOX_FUEL_ROD_DOUBLE, MIItems.HE_MOX_FUEL_ROD_QUAD, MIItems.HE_MOX_FUEL_ROD_DEPLETED,
    MIItems.LEU_FUEL_ROD, MIItems.LEU_FUEL_ROD_DOUBLE, MIItems.LEU_FUEL_ROD_QUAD, MIItems.LEU_FUEL_ROD_DEPLETED,
    MIItems.LE_MOX_FUEL_ROD, MIItems.LE_MOX_FUEL_ROD_DOUBLE, MIItems.LE_MOX_FUEL_ROD_QUAD, MIItems.LE_MOX_FUEL_ROD_DEPLETED,
    Items.SMALL_HEAT_EXCHANGER, Items.LARGE_HEAT_EXCHANGER, Items.CADMIUM_FUEL_ROD, Items.CARBON_LARGE_PLATE
)

val FLUIDS = immutableListOf(
    Fluids.DEUTERIUM,
    Fluids.WATER, Fluids.STEAM,
    Fluids.HEAVY_WATER, Fluids.HEAVY_WATER_STEAM,
    Fluids.HIGH_PRESSURE_WATER, Fluids.HIGH_PRESSURE_STEAM,
    Fluids.HIGH_PRESSURE_HEAVY_WATER, Fluids.HIGH_PRESSURE_HEAVY_WATER_STEAM,
)