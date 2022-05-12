package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ItemVariant extends MatterVariant {

    public ItemVariant(Item item,@NonNull Map<String,Object> tag) {
        super(item, tag);
    }

    public ItemVariant(Item item) {
        super(item);
    }

    public Item getItem() {
        return (Item) this.getMatter();
    }

}
