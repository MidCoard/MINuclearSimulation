package top.focess.mc.mi.nuclear;

import aztech.modern_industrialization.machines.blockentities.hatches.NuclearHatch;
import aztech.modern_industrialization.machines.components.IntegerHistoryComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.nuclear.INuclearTile;
import aztech.modern_industrialization.nuclear.NuclearGridHelper;
import top.focess.mc.mi.nuclear.mc.FluidVariant;
import top.focess.mc.mi.nuclear.mc.MatterVariant;
import top.focess.mc.mi.nuclear.mi.NuclearGrid;

import java.util.function.BiFunction;

public class NuclearSimulation {

    private final NuclearGrid nuclearGrid;

    private final IsActiveComponent isActive;
    private int tickCount = 0;
    private long startTime = System.nanoTime();

    public NuclearSimulation(int size, BiFunction<Integer,Integer, MatterVariant> variantSupplier) {
        INuclearTile[][] tiles = new NuclearHatch[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                MatterVariant matterVariant = variantSupplier.apply(i, j);
                NuclearHatch nuclearHatch = new NuclearHatch(matterVariant instanceof FluidVariant);
                nuclearHatch.getInventory().input().setMatterVariant(matterVariant,1);
                tiles[i][j] = nuclearHatch;
            }
        }
        this.nuclearGrid = new NuclearGrid(size,new IntegerHistoryComponent(new String[] { "euProduction", "euFuelConsumption" },300),tiles );
        this.isActive = new IsActiveComponent();
    }

    public void tick() {
        tickCount++;
        System.out.println((System.nanoTime() - startTime) / 1000000000d + ": " + tickCount + "tick(s)");
        isActive.updateActive(NuclearGridHelper.simulate(nuclearGrid));
        nuclearGrid.tick();
    }
}
