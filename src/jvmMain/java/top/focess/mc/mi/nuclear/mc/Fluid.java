package top.focess.mc.mi.nuclear.mc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Fluid extends Matter{

    private static final List<Fluid> FLUIDS = new ArrayList<>();
    public Fluid(String namespace, String name) {
        super(namespace, name);
        FLUIDS.add(this);
    }

    public static Fluid deserialize(Map<String,Object> map){
        String namespace = (String) map.get("namespace");
        String name = (String) map.get("name");
        return Fluid.getFluid(namespace, name);
    }

    private static Fluid getFluid(String namespace, String name) {
        for (Fluid fluid : FLUIDS)
            if (fluid.getNamespace().equals(namespace) && fluid.getName().equals(name))
                return fluid;
        return null;
    }
}
