package top.focess.mc.mi.nuclear;

import aztech.modern_industrialization.machines.blockentities.hatches.NuclearHatch;
import aztech.modern_industrialization.machines.components.IntegerHistoryComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.nuclear.NuclearGridHelper;
import top.focess.mc.mi.nuclear.mi.NuclearGrid;

public class NuclearSimulation {

    private final NuclearGrid nuclearGrid;

    private final IsActiveComponent isActive;

    public NuclearSimulation(int size) {
        this.nuclearGrid = new NuclearGrid(size,new IntegerHistoryComponent(new String[] { "euProduction", "euFuelConsumption" },300),new NuclearHatch[size][size]);
        this.isActive = new IsActiveComponent();
    }

    public void tick() {
        isActive.updateActive(NuclearGridHelper.simulate(nuclearGrid));
        nuclearGrid.tick();
    }
}
