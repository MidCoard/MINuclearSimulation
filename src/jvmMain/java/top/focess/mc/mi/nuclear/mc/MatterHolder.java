package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;
import top.focess.util.serialize.FocessSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MatterHolder implements FocessSerializable {

    protected final boolean isFluid;
    private Matter matter;
    private long amount;
    private Map<String, Object> tag;
    protected long maxAmount = 0;

    // only for blank matterVariant, if not blank, there is nothing changed
    public MatterHolder(@NonNull MatterVariant matterVariant) {
        this(matterVariant, 0);
    }

    public MatterHolder(@NonNull MatterVariant matterVariant, long amount) {
        this.isFluid = matterVariant instanceof FluidVariant;
        this.setMatterVariant0(matterVariant);
        this.setAmount(amount);
    }

    protected MatterHolder(boolean isFluid, MatterVariant matterVariant, long amount) {
        this.isFluid = isFluid;
        this.setMatterVariant0(matterVariant);
        this.amount = amount;
    }

    public void setAmount(long amount) {
        if (this.matter == null)
            return;
        this.amount = Math.max(Math.min(amount, this.maxAmount),0);
        if (this.amount == 0) {
            this.matter = null;
            this.tag.clear();
        }
    }

    public void setMatterVariant(@NonNull MatterVariant matterVariant, long amount) {
        this.setMatterVariant0(matterVariant);
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

    protected void setMatterVariant0(@NonNull MatterVariant matterVariant) {
        this.matter = matterVariant.getMatter();
        this.tag = new HashMap<>(matterVariant.getTag());
        if (matterVariant instanceof ItemVariant && matterVariant.getMatter() != null)
            this.maxAmount = ((ItemVariant) matterVariant).getItem().getMaxCount();
        else if (matterVariant instanceof FluidVariant)
            this.maxAmount = 1000000L * 81000L;
        else this.maxAmount = 0;
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
        map.put("matterVariant", getMatterVariant());
        map.put("amount", amount);
        return map;
    }

    public static MatterHolder deserialize(Map<String, Object> map) {
        return new MatterHolder(
                (boolean)map.get("isFluid"),
                (MatterVariant) map.get("matterVariant"),
                (long) map.get("amount")
        );
    }
}
