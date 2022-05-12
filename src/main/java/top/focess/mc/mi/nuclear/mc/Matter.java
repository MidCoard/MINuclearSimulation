package top.focess.mc.mi.nuclear.mc;

public abstract class Matter {
    private final String namespace;
    private final String name;
    private final boolean blank;

    public Matter(String namespace, String name, boolean blank) {
        this.namespace = namespace;
        this.name = name;
        this.blank = blank;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public boolean isBlank() {
        return this.blank;
    }
}
