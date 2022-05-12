package top.focess.mc.mi.nuclear.mi;

import top.focess.mc.mi.nuclear.mc.Matter;
import top.focess.mc.mi.nuclear.mc.MatterHolder;

import java.util.ArrayList;
import java.util.List;

public class MINuclearInventory {

    private final boolean isFluid;

    private final List<MatterHolder> matters = new ArrayList<>();

    public MINuclearInventory(boolean isFluid) {
        this.isFluid = isFluid;
    }

    public MatterHolder get(int index) {
        return matters.get(index);
    }
}
