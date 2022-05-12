package top.focess.mc.mi.nuclear.mc;

public class Item extends Matter{
    public Item(String namespace, String name) {
        super(namespace, name, false);
    }

    public Item(String namespace, String name, boolean blank) {
        super(namespace, name, blank);
    }
}
