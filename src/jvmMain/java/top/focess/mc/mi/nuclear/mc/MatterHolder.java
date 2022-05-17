package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;
import top.focess.util.serialize.FocessSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MatterHolder implements FocessSerializable {

    private final boolean isFluid;
    private Matter matter;
    private long amount;
    private Map<String, Object> tag;

    private boolean infinite = false;

    public MatterHolder(@NonNull MatterVariant matterVariant) {
        this.isFluid = matterVariant instanceof FluidVariant;
        this.matter = matterVariant.getMatter();
        this.amount = 0;
        this.tag = new HashMap<>(matterVariant.getTag());
    }

    public MatterHolder(@NonNull MatterVariant matterVariant, long amount) {
        this.isFluid = matterVariant instanceof FluidVariant;
        this.matter = matterVariant.getMatter();
        this.tag = new HashMap<>(matterVariant.getTag());
        this.amount = amount;
        if (matterVariant instanceof ItemVariant && matterVariant.getMatter() != null)
            this.amount = Math.min(this.amount,((ItemVariant) matterVariant).getItem().getMaxCount());
        if (this.amount == 0) {
            this.matter = null;
            this.tag.clear();
        }
    }

    public void setMatterVariant(@NonNull MatterVariant matterVariant, long amount) {
        this.matter = matterVariant.getMatter();
        this.tag = new HashMap<>(matterVariant.getTag());
        if (this.infinite)
            return;
        this.amount = amount;
        if (matterVariant instanceof ItemVariant && matterVariant.getMatter() != null)
            this.amount = Math.min(this.amount,((ItemVariant) matterVariant).getItem().getMaxCount());
        if (this.amount == 0) {
            this.matter = null;
            this.tag.clear();
        }
    }

    public boolean addMatterVariant(@NonNull MatterVariant matterVariant, long amount) {
        if (this.matter == null && isFluid == matterVariant instanceof FluidVariant) {
            this.setMatterVariant(matterVariant, amount);
            return true;
        } else if (this.matter == matterVariant.getMatter() && this.tag.equals(matterVariant.getTag())) {
            if (matterVariant instanceof ItemVariant && matterVariant.getMatter() != null)
                if (this.amount + amount > ((ItemVariant) matterVariant).getItem().getMaxCount())
                    return false;
            this.amount += amount;
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

    public void setMatterVariant(@NonNull MatterVariant matterVariant) {
        this.matter = matterVariant.getMatter();
        this.tag = new HashMap<>(matterVariant.getTag());
    }

    public long getAmount() {
        if (this.infinite)
            if (this.getMatterVariant() instanceof ItemVariant)
                return this.matter == null ? 0 : ((ItemVariant) this.getMatterVariant()).getItem().getMaxCount();
            else
                return this.matter == null ? 0 : 999999L;
        return amount;
    }

    public Map<String, Object> getTag() {
        return this.tag;
    }

    public void empty() {
        if (this.infinite)
            return;
        this.amount = 0;
    }

    public long extract(MatterVariant variant, long actual) {
        if (this.matter != variant.getMatter() || !this.tag.equals(variant.getTag()))
            return 0;
        if (this.infinite)
            return actual;
        long amount = Math.min(this.amount, actual);
        this.amount -= amount;
        return amount;
    }

    @Override
    public String toString() {
        return matter + ":" + amount + ":" + this.tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatterHolder that = (MatterHolder) o;

        if (isFluid != that.isFluid) return false;
        if (amount != that.amount) return false;
        if (!Objects.equals(matter, that.matter)) return false;
        return tag.equals(that.tag);
    }

    @Override
    public int hashCode() {
        int result = (isFluid ? 1 : 0);
        result = 31 * result + (matter != null ? matter.hashCode() : 0);
        result = 31 * result + (int) (amount ^ (amount >>> 32));
        result = 31 * result + tag.hashCode();
        return result;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public void setInfinite(boolean infinite) {
        this.infinite = infinite;
    }
}
