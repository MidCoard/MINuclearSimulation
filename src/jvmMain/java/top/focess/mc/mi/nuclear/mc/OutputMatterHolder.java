package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;

public class OutputMatterHolder extends MatterHolder{

    public OutputMatterHolder(boolean isFluid) {
        super(isFluid ? FluidVariant.blank() : ItemVariant.blank());
    }

    private OutputMatterHolder(boolean isFluid, Matter matter, long amount, Map<String, Object> tag, long maxAmount) {
        super(isFluid, matter, amount, tag, maxAmount);
    }

    // return the rest amount of the matter
    public long insertMatterVariant(@NonNull MatterVariant matterVariant, long amount) {
        if (this.matter == null) {
            long before = this.getAmount();
            if (before != 0)
                throw new IllegalStateException("The matter holder is not empty!");
            this.setMatterVariant(matterVariant, amount);
            return amount - (this.getAmount() - before);
        } else if (this.matter == matterVariant.getMatter() && this.tag.equals(matterVariant.getTag())) {
            long before = this.getAmount();
            this.setAmount(this.getAmount() + amount);
            return amount - (this.getAmount() - before);
        }
        return amount;
    }

    public OutputMatterHolder copy() {
        return new OutputMatterHolder(this.isFluid, this.matter, this.getAmount(), this.tag, this.maxAmount);
    }

    public static OutputMatterHolder deserialize(Map<String, Object> map) {
        return new OutputMatterHolder(
                (boolean)map.get("isFluid"),
                (Matter)map.get("matter"),
                (long) map.get("amount"),
                (Map<String, Object>) map.get("tag"),
                (long) map.get("maxAmount")
        );
    }
}
