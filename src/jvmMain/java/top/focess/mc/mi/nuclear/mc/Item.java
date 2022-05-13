package top.focess.mc.mi.nuclear.mc;

import java.util.ArrayList;
import java.util.List;

public class Item extends Matter{

    private static final List<Item> ITEMS = new ArrayList<>();

    public static Item getItem(String namespace, String name) {
        for (Item item : ITEMS)
            if (item.getNamespace().equals(namespace) && item.getName().equals(name))
                return item;
        throw new IllegalArgumentException("Item not found!");
    }

    //ignore
    private final int maxCount;

    public Item(String namespace, String name) {
        super(namespace, name);
        ITEMS.add(this);
        this.maxCount = 64;
    }

    public Item(String namespace, String name, int maxCount) {
        super(namespace, name);
        ITEMS.add(this);
        this.maxCount = maxCount;
    }

    public int getMaxCount() {
        return maxCount;
    }
}
