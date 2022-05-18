package top.focess.mc.mi.nuclear.mc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InputMatterHolder extends MatterHolder {

    private boolean infinite = false;

    public InputMatterHolder(boolean isFluid) {
        super(isFluid ? FluidVariant.blank() : ItemVariant.blank());
    }

    public InputMatterHolder(boolean infinite, boolean isFluid, Matter matter, long amount, Map<String, Object> tag, long maxAmount) {
        super(isFluid, matter, amount, tag, maxAmount);
        this.infinite = infinite;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public long tryExtract(MatterVariant variant, long actual) {
        if (this.matter != variant.getMatter() || !this.tag.equals(variant.getTag()))
            return 0;
        if (this.infinite)
            return actual;
        return Math.min(this.getAmount(), actual);
    }

    public long extract(MatterVariant variant, long actual) {
        if (this.matter != variant.getMatter() || !this.tag.equals(variant.getTag()))
            return 0;
        if (this.infinite)
            return actual;
        long amount = Math.min(this.getAmount(), actual);
        this.setAmount(this.getAmount() - amount);
        return amount;
    }

    public void setMatterVariant(boolean infinite, @NotNull MatterVariant matterVariant, long amount) {
        this.matter = matterVariant.getMatter();
        this.tag = new HashMap<>(matterVariant.getTag());
        this.infinite = infinite;
        this.setAmount(amount);
        this.checkInfinite();
    }

    private void checkInfinite() {
        if (this.infinite) {
            Objects.requireNonNull(this.matter);
            super.setAmount(this.maxAmount);
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
                (Matter)map.get("matter"),
                (long) map.get("amount"),
                (Map<String, Object>) map.get("tag"),
                (long) map.get("maxAmount")
        );
    }
}
