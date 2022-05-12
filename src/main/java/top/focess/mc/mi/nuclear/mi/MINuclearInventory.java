package top.focess.mc.mi.nuclear.mi;

import top.focess.mc.mi.nuclear.mc.FluidVariant;
import top.focess.mc.mi.nuclear.mc.Item;
import top.focess.mc.mi.nuclear.mc.ItemVariant;
import top.focess.mc.mi.nuclear.mc.MatterHolder;

import java.util.ArrayList;
import java.util.List;

public class MINuclearInventory {

    private final boolean isFluid;

    private final List<MatterHolder> matters = new ArrayList<>();

    public MINuclearInventory(boolean isFluid) {
        this.isFluid = isFluid;
        for (int i = 0;i < 2;i ++)
            if (isFluid)
                matters.add(new MatterHolder(FluidVariant.of(null)));
            else
                matters.add(new MatterHolder(ItemVariant.of((Item) null)));
    }

    public MatterHolder get(int index) {
        return matters.get(index);
    }


}
