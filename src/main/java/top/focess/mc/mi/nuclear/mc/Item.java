package top.focess.mc.mi.nuclear.mc;

public class Item extends Matter{
    private final int maxCount;

    public Item(String namespace, String name) {
        super(namespace, name);
        this.maxCount = 64;
    }

    public Item(String namespace, String name, int maxCount) {
        super(namespace, name);
        this.maxCount = maxCount;
    }

    public int getMaxCount() {
        return maxCount;
    }
}
