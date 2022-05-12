package top.focess.mc.mi.nuclear;

import aztech.modern_industrialization.machines.components.IsActiveComponent;
import top.focess.mc.mi.nuclear.mi.NuclearGrid;

public class NuclearSimulation {

    private final NuclearGrid nuclearGrid;

    private final IsActiveComponent isActive;

    public NuclearSimulation() {
        this.nuclearGrid = new NuclearGrid();
        this.isActive = new IsActiveComponent();
    }

    public void tick() {
        
    }
}
