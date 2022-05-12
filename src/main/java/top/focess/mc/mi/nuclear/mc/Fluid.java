package top.focess.mc.mi.nuclear.mc;

public class Fluid extends Matter{
    public Fluid(String namespace, String name) {
        super(namespace, name, false);
    }

    public Fluid(String namespace, String name, boolean isBlank) {
        super(namespace, name, isBlank);
    }
}
