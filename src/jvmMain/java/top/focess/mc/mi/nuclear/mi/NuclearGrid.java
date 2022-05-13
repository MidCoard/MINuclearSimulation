package top.focess.mc.mi.nuclear.mi;

import aztech.modern_industrialization.machines.components.IntegerHistoryComponent;
import aztech.modern_industrialization.nuclear.INuclearGrid;
import aztech.modern_industrialization.nuclear.INuclearTile;
import aztech.modern_industrialization.nuclear.NeutronFate;
import aztech.modern_industrialization.nuclear.NeutronType;

import java.util.Optional;

public class NuclearGrid implements INuclearGrid {

    private final int size;
    private final IntegerHistoryComponent efficiencyHistory;
    private final INuclearTile[][] hatchesGrid;

    public NuclearGrid(int size, IntegerHistoryComponent efficiencyHistory, INuclearTile[][] hatchesGrid) {
        this.size = size;
        this.efficiencyHistory = efficiencyHistory;
        this.hatchesGrid = hatchesGrid;
    }

    @Override
    public int getSizeX() {
        return size;
    }

    @Override
    public int getSizeY() {
        return size;
    }

    @Override
    public Optional<INuclearTile> getNuclearTile(int x, int y) {
        return Optional.ofNullable(hatchesGrid[x][y]);
    }

    @Override
    public void registerNeutronFate(int neutronNumber, NeutronType type, NeutronFate escape) {
    }

    @Override
    public void registerNeutronCreation(int neutronNumber, NeutronType type) {
    }

    @Override
    public void registerEuFuelConsumption(double eu) {
        efficiencyHistory.addValue("euFuelConsumption", (int) eu);

    }

    @Override
    public void registerEuProduction(double eu) {
        efficiencyHistory.addValue("euProduction", (int) eu);
    }

    public void tick() {
        this.efficiencyHistory.tick();
        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++)
                if (hatchesGrid[x][y] != null)
                    hatchesGrid[x][y].tick();
    }

    public IntegerHistoryComponent getEfficiencyHistory() {
        return efficiencyHistory;
    }
}