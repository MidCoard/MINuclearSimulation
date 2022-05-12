package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class MatterVariant {

    private final Matter matter;
    private final Map<String, Object> tag;

    public MatterVariant(Matter matter, @NonNull Map<String,Object> tag) {
        this.matter = matter;
        this.tag = tag;
    }

    public MatterVariant(Matter matter) {
        this.matter = matter;
        this.tag = new HashMap<>();
    }

    public boolean isBlank() {
        return matter == null;
    }

    public Matter getMatter() {
        return matter;
    }

    public Map<String, Object> getTag() {
        return tag;
    }


    public MatterHolder toStack(long amount) {
        return new MatterHolder(this, amount);
    }
}
