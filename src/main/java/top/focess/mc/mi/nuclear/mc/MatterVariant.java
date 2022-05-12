package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class MatterVariant {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatterVariant variant = (MatterVariant) o;

        return Objects.equals(matter, variant.matter);
    }

    @Override
    public int hashCode() {
        return matter != null ? matter.hashCode() : 0;
    }

    public abstract MatterVariant of(Map<String, Object> tag);
}
