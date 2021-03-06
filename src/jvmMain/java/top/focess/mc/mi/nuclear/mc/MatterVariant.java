package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;
import top.focess.util.serialize.FocessSerializable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class MatterVariant implements FocessSerializable {

    private final Matter matter;
    private final Map<String, Object> tag;

    public MatterVariant(Matter matter, @NonNull Map<String, Object> tag) {
        this.matter = matter;
        this.tag = new HashMap<>(tag);
    }

    public MatterVariant(Matter matter) {
        this.matter = matter;
        this.tag = new HashMap<>();
    }

    public static MatterVariant of(@NonNull Matter matter) {
        if (matter instanceof Item)
            return ItemVariant.of((Item) matter);
        else if (matter instanceof Fluid)
            return FluidVariant.of((Fluid) matter);
        throw new IllegalArgumentException("Matter is not Item or Fluid");
    }

    public boolean isBlank() {
        return matter == null;
    }

    @Nullable
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

    @Nullable
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("matter", this.matter);
        map.put("tag", this.tag);
        return map;
    }
}
