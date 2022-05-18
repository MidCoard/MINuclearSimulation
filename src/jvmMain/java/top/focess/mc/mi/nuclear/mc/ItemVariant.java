package top.focess.mc.mi.nuclear.mc;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;

public class ItemVariant extends MatterVariant {

    public ItemVariant(Item item, @NonNull Map<String, Object> tag) {
        super(item, tag);
    }

    public ItemVariant(Item item) {
        super(item);
    }

    public static ItemVariant of(MatterHolder stack) {
        return of((Item) stack.getMatterVariant().getMatter(), stack.getTag());
    }

    public static ItemVariant blank() {
        return ItemVariant.of((Item) null);
    }

    public static ItemVariant of(Item item) {
        return new ItemVariant(item);
    }

    public static ItemVariant of(Item item, @NonNull Map<String, Object> tag) {
        if (item == null)
            return blank();
        return new ItemVariant(item, tag);
    }

    public Item getItem() {
        return (Item) this.getMatter();
    }

}
