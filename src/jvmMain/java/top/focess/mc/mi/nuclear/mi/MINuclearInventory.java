package top.focess.mc.mi.nuclear.mi;

import top.focess.mc.mi.nuclear.mc.FluidVariant;
import top.focess.mc.mi.nuclear.mc.ItemVariant;
import top.focess.mc.mi.nuclear.mc.MatterHolder;
import top.focess.mc.mi.nuclear.mc.MatterVariant;
import top.focess.util.serialize.FocessSerializable;

import java.util.ArrayList;
import java.util.List;

public class MINuclearInventory implements FocessSerializable {

    private final List<MatterHolder> matters = new ArrayList<>();

    public MINuclearInventory(boolean isFluid) {
        for (int i = 0;i < 2;i ++)
            if (isFluid)
                matters.add(new MatterHolder(FluidVariant.blank()));
            else
                matters.add(new MatterHolder(ItemVariant.blank()));
    }

    public MatterHolder input() {
        return matters.get(0);
    }

    public long output(MatterVariant matterVariant, long amount) {
        for (int i = 1;i<matters.size();i++)
            if (matters.get(i).addMatterVariant(matterVariant, amount))
                return amount;
        this.matters.add(new MatterHolder(matterVariant, amount));
        return amount;
    }


}
