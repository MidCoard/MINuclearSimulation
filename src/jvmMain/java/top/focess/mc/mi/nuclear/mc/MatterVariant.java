package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class MatterVariant {

    private final Matter matter;
    private final Map<String, Object> tag;

    public MatterVariant(Matter matter, @NonNull Map<String,Object> tag) {
        this.matter = matter;
        this.tag = new HashMap<>(tag);
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
        return Collections.unmodifiableMap(tag);
    }

    public MatterHolder toStack(long amount) {
        return new MatterHolder(this, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MatterVariant variant = (MatterVariant) o;

        if (!Objects.equals(matter, variant.matter)) return false;
        return tag.equals(variant.tag);
    }

    @Override
    public int hashCode() {
        int result = matter != null ? matter.hashCode() : 0;
        result = 31 * result + tag.hashCode();
        return result;
    }

    @Override
    public String toString() {
        if (this.isBlank())
            return "Empty";
        else
            return this.matter.toString() + ":" + this.tag;
    }

    public static MatterVariant of(@NonNull Matter matter) {
        if (matter instanceof Item)
            return ItemVariant.of((Item) matter);
        else if (matter instanceof Fluid)
            return FluidVariant.of((Fluid) matter);
        throw new IllegalArgumentException("Matter is not Item or Fluid");
    }
}
