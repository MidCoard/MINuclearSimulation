package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class MatterHolder {

    private MatterVariant matterVariant;
    private final AtomicLong amount;
    private final Map<String,Object> tag;

    public MatterHolder(@NonNull MatterVariant matterVariant) {
        this.matterVariant = matterVariant;
        this.amount = new AtomicLong(0);
        this.tag = new HashMap<>(matterVariant.getTag());
    }

    public MatterHolder(MatterVariant matterVariant, long amount) {
        this.matterVariant = matterVariant;
        this.amount = new AtomicLong(amount);
        this.tag = new HashMap<>(matterVariant.getTag());
    }

    public void setMatterVariant(@NonNull MatterVariant matterVariant, long amount) {
        this.matterVariant = matterVariant;
        this.amount.set(amount);
    }

    public void setMatterVariant(@NonNull MatterVariant matter) {
        this.matterVariant = matter;
        this.amount.set(1);
    }

    public void addMatterVariant(@NonNull MatterVariant matter, long amount) {
        if (this.matterVariant.isBlank())
            this.matterVariant = matter;
        else if (!this.matterVariant.equals(matter))
            throw new IllegalArgumentException("MatterVariant is not same!");
        this.amount.addAndGet(amount);
    }

    public MatterVariant getMatterVariant() {
        return this.matterVariant.of(this.tag);
    }

    public long getAmount() {
        return amount.get();
    }

    public Map<String, Object> getTag() {
        return this.tag;
    }
}
