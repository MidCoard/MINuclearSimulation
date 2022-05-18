package top.focess.mc.mi.nuclear.mc;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Fluid extends Matter {

    private static final List<Fluid> FLUIDS = new ArrayList<>();
    private final Color color;

    public Fluid(String namespace, String name, Color color) {
        super(namespace, name);
        this.color = color;
        FLUIDS.add(this);
    }

    public static Fluid deserialize(Map<String, Object> map) {
        String namespace = (String) map.get("namespace");
        String name = (String) map.get("name");
        return Fluid.getFluid(namespace, name);
    }

    private static Fluid getFluid(String namespace, String name) {
        for (Fluid fluid : FLUIDS)
            if (fluid.getNamespace().equals(namespace) && fluid.getName().equals(name))
                return fluid;
        throw new IllegalArgumentException("Fluid not found!");
    }

    public Color getColor() {
        return color;
    }
}
