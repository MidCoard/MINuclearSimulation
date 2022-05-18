package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;

public class FluidVariant extends MatterVariant {
    public FluidVariant(Matter matter, @NonNull Map<String, Object> tag) {
        super(matter, tag);
    }

    public FluidVariant(Matter matter) {
        super(matter);
    }

    public static FluidVariant blank() {
        return FluidVariant.of(null);
    }

    public static FluidVariant of(Fluid fluid) {
        return new FluidVariant(fluid);
    }

    public static FluidVariant of(Fluid fluid, @NonNull Map<String, Object> tag) {
        if (fluid == null)
            return blank();
        return new FluidVariant(fluid, tag);
    }

    public Fluid getFluid() {
        return (Fluid) this.getMatter();
    }

    public static FluidVariant deserialize(@NonNull Map<String, Object> map) {
        return FluidVariant.of((Fluid) map.get("matter"), (Map<String, Object>) map.get("tag"));
    }
}
