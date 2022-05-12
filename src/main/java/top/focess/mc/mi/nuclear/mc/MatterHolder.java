package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;

public class MatterHolder {

    private Matter matter;
    private long amount;

    public MatterHolder(@NonNull Matter matter, long amount) {
        this.matter = matter;
        this.amount = amount;
    }

    public void setMatter(@NonNull Matter matter, long amount) {
        this.matter = matter;
        this.amount = amount;
    }

    public void setMatter(Matter matter) {
        this.matter = matter;
        this.amount = 1;
    }

    public Matter getMatter() {
        return matter;
    }

    public long getAmount() {
        return amount;
    }
}
