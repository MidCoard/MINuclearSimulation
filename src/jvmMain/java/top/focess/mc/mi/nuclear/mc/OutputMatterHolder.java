package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

public class OutputMatterHolder extends MatterHolder{

    private long takeout = 0;
    private long outputMaxAmount;

    public OutputMatterHolder(boolean isFluid) {
        super(isFluid ? FluidVariant.blank() : ItemVariant.blank());
        this.setEqualOutputMaxAmount(-1);
    }

    private OutputMatterHolder(boolean isFluid, MatterVariant matterVariant, long amount, long takeout,long outputMaxAmount) {
        super(isFluid, matterVariant, amount);
        this.takeout = takeout;
        this.outputMaxAmount = outputMaxAmount;
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
        return new OutputMatterHolder(this.isFluid, this.getMatterVariant(), this.getAmount(), this.takeout, this.outputMaxAmount);
    }

    @Override
    public @Nullable Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();
        Objects.requireNonNull(map);
        map.put("takeout", takeout);
        map.put("outputMaxAmount", outputMaxAmount);
        return map;
    }

    public static OutputMatterHolder deserialize(Map<String, Object> map) {
        return new OutputMatterHolder(
                (boolean)map.get("isFluid"),
                (MatterVariant) map.get("matterVariant"),
                Long.parseLong(map.get("amount").toString()),
                Long.parseLong(map.get("takeout").toString()),
                Long.parseLong(map.get("outputMaxAmount").toString())
        );
    }

    @Override
    public void setAmount(long amount) {
        long temp = this.maxAmount;
        this.maxAmount = (outputMaxAmount < 0 ? this.maxAmount : outputMaxAmount);
        super.setAmount(amount);
        this.maxAmount = temp;
    }

    public long getTakeout() {
        return takeout;
    }

    public long getEqualTakeout() {
        return this.isFluid ? this.takeout / 81 : this.takeout;
    }

    public void setTakeout(long takeout) {
        this.takeout = takeout;
    }

    public void setOutputMaxAmount(long outputMaxAmount) {
        this.outputMaxAmount = outputMaxAmount;
        this.setAmount(this.getAmount());
    }

    public long getOutputMaxAmount() {
        return outputMaxAmount;
    }

    public long getEqualOutputMaxAmount() {
        return this.isFluid ? this.outputMaxAmount / 81 : this.outputMaxAmount;
    }

    public void setEqualOutputMaxAmount(long outputMaxAmount) {
        this.outputMaxAmount = this.isFluid ? outputMaxAmount * 81 : outputMaxAmount;
        this.setAmount(this.getAmount());
    }

    public void setEqualTakeout(long takeout) {
        this.takeout = this.isFluid ? takeout * 81 : takeout;
    }

    public long extractAmount(long amount) {
        long before = this.getAmount();
        this.setAmount(this.getAmount() - amount);
        return before - this.getAmount();
    }
}
