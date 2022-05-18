package top.focess.mc.mi.nuclear.mi;

import aztech.modern_industrialization.machines.components.IntegerHistoryComponent;
import aztech.modern_industrialization.nuclear.INuclearGrid;
import aztech.modern_industrialization.nuclear.INuclearTile;
import aztech.modern_industrialization.nuclear.NeutronFate;
import aztech.modern_industrialization.nuclear.NeutronType;
import top.focess.util.serialize.FocessSerializable;

import java.util.Arrays;
import java.util.Optional;

public class NuclearGrid implements INuclearGrid, FocessSerializable {

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

    public void setNuclearTile(int x, int y, INuclearTile iNuclearTile) {
        this.hatchesGrid[x][y] = iNuclearTile;
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
    }

    public IntegerHistoryComponent getEfficiencyHistory() {
        return efficiencyHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NuclearGrid that = (NuclearGrid) o;

        if (size != that.size) return false;
        if (!efficiencyHistory.equals(that.efficiencyHistory)) return false;
        return Arrays.deepEquals(hatchesGrid, that.hatchesGrid);
    }

    @Override
    public int hashCode() {
        int result = size;
        result = 31 * result + efficiencyHistory.hashCode();
        result = 31 * result + Arrays.deepHashCode(hatchesGrid);
        return result;
    }
}
