package top.focess.mc.mi.nuclear;

import aztech.modern_industrialization.machines.blockentities.hatches.NuclearHatch;
import aztech.modern_industrialization.machines.components.IntegerHistoryComponent;
import aztech.modern_industrialization.machines.components.IsActiveComponent;
import aztech.modern_industrialization.nuclear.INuclearTile;
import aztech.modern_industrialization.nuclear.NuclearGridHelper;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.Nullable;
import top.focess.mc.mi.nuclear.mc.FluidVariant;
import top.focess.mc.mi.nuclear.mc.MatterVariant;
import top.focess.mc.mi.nuclear.mi.NuclearGrid;
import top.focess.mc.mi.nuclear.mi.NuclearReactionType;
import top.focess.util.serialize.FocessSerializable;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class NuclearSimulation implements FocessSerializable {
    private final NuclearGrid nuclearGrid;

    private final IsActiveComponent isActive;
    private int tickCount = 0;
    private final long startTime = System.nanoTime();

    public NuclearSimulation(int size, BiFunction<Integer,Integer, MatterVariant> variantSupplier, NuclearReactionType nuclearReactionType) {
        BiPredicate<Integer, Integer> isValid = nuclearReactionType.getReaction();
        INuclearTile[][] tiles = new NuclearHatch[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                if (isValid.test(i, j)) {
                    MatterVariant matterVariant = variantSupplier.apply(i, j);
                    NuclearHatch nuclearHatch = new NuclearHatch(matterVariant instanceof FluidVariant);
                    nuclearHatch.getInventory().input().setMatterVariant(matterVariant,1);
                    tiles[i][j] = nuclearHatch;
                }
        this.nuclearGrid = new NuclearGrid(size,new IntegerHistoryComponent(new String[] { "euProduction", "euFuelConsumption" },300),tiles );
        this.isActive = new IsActiveComponent();
    }

    private NuclearSimulation(NuclearGrid grid, IsActiveComponent isActive, int tickCount) {
        this.nuclearGrid = grid;
        this.isActive = isActive;
        this.tickCount = tickCount;
    }

    public void tick() {
        tickCount++;
        System.out.println((System.nanoTime() - startTime) / 1000000000d + ": " + tickCount + "tick(s)");
        isActive.updateActive(NuclearGridHelper.simulate(nuclearGrid));
        nuclearGrid.tick();
    }

    @Nullable
    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> map = Maps.newHashMap();
        map.put("tickCount",tickCount);
        map.put("nuclearGrid",nuclearGrid.serialize());
        map.put("isActive",isActive.serialize());
        return FocessSerializable.super.serialize();
    }
}
//stop