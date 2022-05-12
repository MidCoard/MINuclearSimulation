package top.focess.mc.mi.nuclear.mc;

import aztech.modern_industrialization.nuclear.INeutronBehaviour;
import aztech.modern_industrialization.nuclear.NuclearAbsorbable;
import aztech.modern_industrialization.nuclear.NuclearComponentItem;
import aztech.modern_industrialization.nuclear.NuclearConstant;

public class Items {

    public static final Item CARBON_LARGE_PLATE =  NuclearAbsorbable.of("carbon_large_plate", 2500, 2 *NuclearConstant.BASE_HEAT_CONDUCTION,
                                                            INeutronBehaviour.of(NuclearConstant.ScatteringType.MEDIUM, NuclearConstant.CARBON,
                                                                        2));

    public static final Item CADMIUM_FUEL_ROD =  NuclearAbsorbable.of("cadmium_fuel_rod", 1900, 0.5 * NuclearConstant.BASE_HEAT_CONDUCTION,
                    INeutronBehaviour.of(NuclearConstant.ScatteringType.HEAVY, NuclearConstant.CADMIUM,
                            1));

    public static final Item SMALL_HEAT_EXCHANGER = NuclearComponentItem.of("small_heat_exchanger", 2500, 15 * NuclearConstant.BASE_HEAT_CONDUCTION,
            INeutronBehaviour.NO_INTERACTION);

    public static final Item LARGE_HEAT_EXCHANGER = NuclearComponentItem.of("large_heat_exchanger", 1800, 30 * NuclearConstant.BASE_HEAT_CONDUCTION,
            INeutronBehaviour.NO_INTERACTION);
}
