package top.focess.mc.mi.nuclear.mc;

public abstract class Matter {
    private final String namespace;
    private final String name;

    public Matter(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return namespace + ":" + name;
    }
}
