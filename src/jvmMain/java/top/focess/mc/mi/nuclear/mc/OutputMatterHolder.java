package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;

public class OutputMatterHolder extends MatterHolder{

    public OutputMatterHolder(boolean isFluid) {
        super(isFluid ? FluidVariant.blank() : ItemVariant.blank());
    }

    private OutputMatterHolder(boolean isFluid, MatterVariant matterVariant, long amount) {
        super(isFluid, matterVariant, amount);
    }

    // return the rest amount of the matter
    public long insertMatterVariant(@NonNull MatterVariant matterVariant, long amount) {
        if (this.getMatterVariant().isBlank()) {
            long before = this.getAmount();
            if (before != 0)
                throw new IllegalStateException("The matter holder is not empty!");
            this.setMatterVariant(matterVariant, amount);
            return amount - (this.getAmount() - before);
        } else if (this.getMatterVariant().equals(matterVariant)) {
            long before = this.getAmount();
            this.setAmount(this.getAmount() + amount);
            return amount - (this.getAmount() - before);
        }
        return amount;
    }

    public OutputMatterHolder copy() {
        return new OutputMatterHolder(this.isFluid, this.getMatterVariant(), this.getAmount());
    }

    public static OutputMatterHolder deserialize(Map<String, Object> map) {
        return new OutputMatterHolder(
                (boolean)map.get("isFluid"),
                (MatterVariant) map.get("matterVariant"),
                (long) map.get("amount")
        );
    }
}
