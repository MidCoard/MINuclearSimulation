package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MatterHolder {

    private Matter matter;
    // seems like this is useless
    private final AtomicLong amount;
    private Map<String,Object> tag;

    public MatterHolder(@NonNull MatterVariant matterVariant) {
        this.matter = matterVariant.getMatter();
        this.amount = new AtomicLong(0);
        this.tag = new HashMap<>(matterVariant.getTag());
    }

    public MatterHolder(@NonNull MatterVariant matterVariant, long amount) {
        this.matter = matterVariant.getMatter();
        this.amount = new AtomicLong(amount);
        this.tag = new HashMap<>(matterVariant.getTag());
    }

    public void setMatterVariant(@NonNull MatterVariant matterVariant, long amount) {
        this.matter = matterVariant.getMatter();
        this.tag = new HashMap<>(matterVariant.getTag());
        this.amount.set(amount);
    }

    public void setMatterVariant(@NonNull MatterVariant matter) {
        this.matter = matter.getMatter();
        this.tag = new HashMap<>(matter.getTag());
    }

    public boolean addMatterVariant(@NonNull MatterVariant matter, long amount) {
        if (this.matter == null) {
            this.setMatterVariant(matter, amount);
            return true;
        }
        else if (this.matter.equals(matter.getMatter())) {
            this.amount.addAndGet(amount);
            return true;
        }
        return false;
    }

    public MatterVariant getMatterVariant() {
        if (matter instanceof Item)
            return ItemVariant.of((Item) matter, this.tag);
        else return FluidVariant.of((Fluid) matter, this.tag);
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
}
