package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;
import top.focess.util.serialize.FocessSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MatterHolder implements FocessSerializable {

    protected final boolean isFluid;
    protected Matter matter;
    private long amount;
    protected Map<String, Object> tag;
    protected long maxAmount = 0;

    // only for blank matterVariant, if not blank, there is nothing changed
    public MatterHolder(@NonNull MatterVariant matterVariant) {
        this(matterVariant, 0);
    }

    public MatterHolder(@NonNull MatterVariant matterVariant, long amount) {
        this.isFluid = matterVariant instanceof FluidVariant;
        this.matter = matterVariant.getMatter();
        this.tag = new HashMap<>(matterVariant.getTag());
        if (matterVariant instanceof ItemVariant && matterVariant.getMatter() != null)
            this.maxAmount = ((ItemVariant) matterVariant).getItem().getMaxCount();
        else if (matterVariant instanceof FluidVariant)
            this.maxAmount = 999999 * 81000L;
        this.setAmount(amount);
    }

    protected MatterHolder(boolean isFluid, Matter matter, long amount, Map<String, Object> tag, long maxAmount) {
        this.isFluid = isFluid;
        this.matter = matter;
        this.amount = amount;
        this.tag = tag;
        this.maxAmount = maxAmount;
    }

    public void setAmount(long amount) {
        if (this.matter == null)
            return;
        this.amount = Math.min(amount, this.maxAmount);
        if (this.amount == 0) {
            this.matter = null;
            this.tag.clear();
        }
    }

    public void setMatterVariant(@NonNull MatterVariant matterVariant, long amount) {
        this.matter = matterVariant.getMatter();
        this.tag = new HashMap<>(matterVariant.getTag());
        this.setAmount(amount);
    }

    public MatterVariant getMatterVariant() {
        if (isFluid)
            return FluidVariant.of((Fluid) matter, tag);
        else
            return ItemVariant.of((Item) matter, tag);
    }

    public void setMatterVariant(@NonNull MatterVariant matterVariant) {
        this.setMatterVariant(matterVariant, this.amount);
    }

    public long getAmount() {
        return amount;
    }

    public Map<String, Object> getTag() {
        return this.tag;
    }

    public void empty() {
        this.setAmount(0);
    }

    public boolean isFluid() {
        return isFluid;
    }

    public void setMaxAmount(long maxAmount) {
        this.maxAmount = maxAmount;
        this.setAmount(this.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatterHolder that = (MatterHolder) o;

        if (isFluid != that.isFluid) return false;
        if (amount != that.amount) return false;
        if (maxAmount != that.maxAmount) return false;
        if (!Objects.equals(matter, that.matter)) return false;
        return tag.equals(that.tag);
    }

    @Override
    public int hashCode() {
        int result = (isFluid ? 1 : 0);
        result = 31 * result + (matter != null ? matter.hashCode() : 0);
        result = 31 * result + (int) (amount ^ (amount >>> 32));
        result = 31 * result + tag.hashCode();
        result = 31 * result + (int) (maxAmount ^ (maxAmount >>> 32));
        return result;
    }

    @Nullable
    @Override
    public Map<String, Object> serialize() {
        Map<String,Object> map = new HashMap<>();
        map.put("isFluid", isFluid);
        map.put("matter", matter);
        map.put("amount", amount);
        map.put("tag", tag);
        map.put("maxAmount", maxAmount);
        return map;
    }

    public static MatterHolder deserialize(Map<String, Object> map) {
        return new MatterHolder(
                (boolean)map.get("isFluid"),
                (Matter)map.get("matter"),
                (long) map.get("amount"),
                (Map<String, Object>) map.get("tag"),
                (long) map.get("maxAmount")
        );
    }
}
