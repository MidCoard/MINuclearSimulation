package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MatterHolder {

    private final boolean isFluid;
    private Matter matter;
    // seems like this is useless
    private final AtomicLong amount;
    private Map<String,Object> tag;

    public MatterHolder(@NonNull MatterVariant matterVariant) {
        this.isFluid = matterVariant instanceof FluidVariant;
        this.matter = matterVariant.getMatter();
        this.amount = new AtomicLong(0);
        this.tag = new HashMap<>(matterVariant.getTag());
    }

    public MatterHolder(@NonNull MatterVariant matterVariant, long amount) {
        this.isFluid = matterVariant instanceof FluidVariant;
        this.matter = matterVariant.getMatter();
        this.amount = new AtomicLong(amount);
        this.tag = new HashMap<>(matterVariant.getTag());
    }

    public void setMatterVariant(@NonNull MatterVariant matterVariant, long amount) {
        this.matter = matterVariant.getMatter();
        this.tag = new HashMap<>(matterVariant.getTag());
        this.amount.set(amount);
    }

    public void setMatterVariant(@NonNull MatterVariant matterVariant) {
        this.matter = matterVariant.getMatter();
        this.tag = new HashMap<>(matterVariant.getTag());
    }

    public boolean addMatterVariant(@NonNull MatterVariant matterVariant, long amount) {
        if (this.matter == null && isFluid == matterVariant instanceof FluidVariant) {
            this.setMatterVariant(matterVariant, amount);
            return true;
        }
        else if (this.matter == matterVariant.getMatter() && this.tag.equals(matterVariant.getTag())) {
            this.amount.addAndGet(amount);
            return true;
        }
        return false;
    }

    public MatterVariant getMatterVariant() {
        if (isFluid)
            return FluidVariant.of((Fluid) matter, tag);
        else
            return ItemVariant.of((Item) matter, tag);
    }

    public long getAmount() {
        return amount.get();
    }

    public Map<String, Object> getTag() {
        return this.tag;
    }

    public void empty() {
        this.amount.set(0);
    }

    public long extract(MatterVariant variant, long actual) {
        if (this.matter != variant.getMatter() || this.tag.equals(variant.getTag()))
            return 0;
        long amount = this.amount.get();
        return Math.min(amount, actual);
    }

    @Override
    public String toString() {
        return matter + ":" + amount + ":" + this.tag;
    }
}
