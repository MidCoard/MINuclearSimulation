package top.focess.mc.mi.nuclear.mi;

import aztech.modern_industrialization.nuclear.INeutronBehaviour;
import aztech.modern_industrialization.nuclear.NuclearConstant;
import aztech.modern_industrialization.nuclear.NuclearFuel;
import top.focess.mc.mi.nuclear.mc.Item;

public class MIItems {

    public static Item of(String name,String materialName, final NuclearConstant.IsotopeFuelParams params, FuelRodType type) {
        if (type == FuelRodType.DEPLETED)
            return new Item("modern-industrialization" , name);
        NuclearFuel.NuclearFuelParams fuelParams = new NuclearFuel.NuclearFuelParams(NuclearConstant.DESINTEGRATION_BY_ROD * type.size, params.maxTemp, params.tempLimitLow,
                params.tempLimitHigh, params.neutronsMultiplication, params.directEnergyFactor, type.size);
        INeutronBehaviour neutronBehaviour = INeutronBehaviour.of(NuclearConstant.ScatteringType.HEAVY, params, type.size);
        return NuclearFuel.of(name,fuelParams, neutronBehaviour, materialName + "_fuel_rod_depleted");
    }

    public static Item U_FUEL_ROD = of("u_fuel_rod","u", NuclearConstant.U, FuelRodType.SIMPLE);
    public static Item U_FUEL_ROD_DEPLETED = of("u_fuel_rod_depleted","u", NuclearConstant.U, FuelRodType.DEPLETED);
    public static Item U_FUEL_ROD_DOUBLE = of("u_fuel_rod_double","u", NuclearConstant.U, FuelRodType.DOUBLE);
    public static Item U_FUEL_ROD_QUAD = of("u_fuel_rod_quad","u", NuclearConstant.U, FuelRodType.QUAD);

    public static Item LEU_FUEL_ROD = of("leu_fuel_rod","leu", NuclearConstant.LEU, FuelRodType.SIMPLE);
    public static Item LEU_FUEL_ROD_DEPLETED = of("leu_fuel_rod_depleted","leu", NuclearConstant.LEU, FuelRodType.DEPLETED);
    public static Item LEU_FUEL_ROD_DOUBLE = of("leu_fuel_rod_double","leu", NuclearConstant.LEU, FuelRodType.DOUBLE);
    public static Item LEU_FUEL_ROD_QUAD = of("leu_fuel_rod_quad","leu", NuclearConstant.LEU, FuelRodType.QUAD);

    public static Item HEU_FUEL_ROD = of("heu_fuel_rod","heu", NuclearConstant.HEU, FuelRodType.SIMPLE);
    public static Item HEU_FUEL_ROD_DEPLETED = of("heu_fuel_rod_depleted","heu", NuclearConstant.HEU, FuelRodType.DEPLETED);
    public static Item HEU_FUEL_ROD_DOUBLE = of("heu_fuel_rod_double","heu", NuclearConstant.HEU, FuelRodType.DOUBLE);
    public static Item HEU_FUEL_ROD_QUAD = of("heu_fuel_rod_quad","heu", NuclearConstant.HEU, FuelRodType.QUAD);

    public static Item LE_MOX_FUEL_ROD = of("le_mox_fuel_rod","le_mox", NuclearConstant.LE_MOX, FuelRodType.SIMPLE);
    public static Item LE_MOX_FUEL_ROD_DEPLETED = of("le_mox_fuel_rod_depleted","le_mox", NuclearConstant.LE_MOX, FuelRodType.DEPLETED);
    public static Item LE_MOX_FUEL_ROD_DOUBLE = of("le_mox_fuel_rod_double","le_mox", NuclearConstant.LE_MOX, FuelRodType.DOUBLE);
    public static Item LE_MOX_FUEL_ROD_QUAD = of("le_mox_fuel_rod_quad","le_mox", NuclearConstant.LE_MOX, FuelRodType.QUAD);

    public static Item HE_MOX_FUEL_ROD = of("he_mox_fuel_rod","he_mox", NuclearConstant.HE_MOX, FuelRodType.SIMPLE);
    public static Item HE_MOX_FUEL_ROD_DEPLETED = of("he_mox_fuel_rod_depleted","he_mox", NuclearConstant.HE_MOX, FuelRodType.DEPLETED);
    public static Item HE_MOX_FUEL_ROD_DOUBLE = of("he_mox_fuel_rod_double","he_mox", NuclearConstant.HE_MOX, FuelRodType.DOUBLE);
    public static Item HE_MOX_FUEL_ROD_QUAD = of("he_mox_fuel_rod_quad","he_mox", NuclearConstant.HE_MOX, FuelRodType.QUAD);

}
