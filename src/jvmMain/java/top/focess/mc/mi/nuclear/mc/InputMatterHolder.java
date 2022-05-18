package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class InputMatterHolder extends MatterHolder {

    private boolean infinite = false;

    public InputMatterHolder(boolean isFluid) {
        super(isFluid ? FluidVariant.blank() : ItemVariant.blank());
    }

    public InputMatterHolder(boolean infinite, boolean isFluid,MatterVariant matterVariant ,long amount) {
        super(isFluid, matterVariant, amount);
        this.infinite = infinite;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public long tryExtract(MatterVariant variant, long actual) {
        if (!this.getMatterVariant().equals(variant))
            return 0;
        if (this.infinite)
            return actual;
        return Math.min(this.getAmount(), actual);
    }

    public long extract(MatterVariant variant, long actual) {
        if (!this.getMatterVariant().equals(variant))
            return 0;
        if (this.infinite)
            return actual;
        long amount = Math.min(this.getAmount(), actual);
        this.setAmount(this.getAmount() - amount);
        return amount;
    }

    public void setMatterVariant(boolean infinite, @NotNull MatterVariant matterVariant, long amount) {
        this.setMatterVariant0(matterVariant);
        this.infinite = infinite;
        this.setAmount(amount);
        this.checkInfinite();
    }

    private void checkInfinite() {
        if (this.infinite) {
            if (!this.getMatterVariant().isBlank()) {
                super.setAmount(this.maxAmount);
                this.getTag().clear();
            }
            else throw new IllegalStateException("Infinite matter can't have blank matter variant");
        }
    }

    @Override
    public void setAmount(long amount) {
        if (this.infinite)
            return;
        super.setAmount(amount);
    }

    @Override
    public @Nullable Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        Objects.requireNonNull(map);
        map.put("infinite", this.infinite);
        return map;
    }

    public static InputMatterHolder deserialize(Map<String, Object> map) {
        return new InputMatterHolder(
                (boolean) map.get("infinite"),
                (boolean)map.get("isFluid"),
                (MatterVariant) map.get("matterVariant"),
                (long) map.get("amount")
        );
    }

    @Override
    protected void setMatterVariant0(@NonNull MatterVariant matterVariant) {
        super.setMatterVariant0(matterVariant);
        this.checkInfinite();
    }
}
