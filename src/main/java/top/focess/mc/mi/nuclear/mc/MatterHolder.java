package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;

public class MatterHolder {

    private MatterVariant matterVariant;
    private long amount;

    public MatterHolder(MatterVariant matterVariant, long amount) {
        this.matterVariant = matterVariant;
        this.amount = amount;
    }

    public void setMatterVariant(@NonNull MatterVariant matterVariant, long amount) {
        this.matterVariant = matterVariant;
        this.amount = amount;
    }

    public void setMatterVariant(MatterVariant matter) {
        this.matterVariant = matter;
        this.amount = 1;
    }

    public MatterVariant getMatterVariant() {
        return matterVariant;
    }

    public long getAmount() {
        return amount;
    }

}
