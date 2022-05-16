package top.focess.mc.mi.nuclear.mi;

public enum FuelRodType {
    DEPLETED(0, "fuel_rod_depleted"),
    SIMPLE(1, "fuel_rod"),
    DOUBLE(2, "fuel_rod_double"),
    QUAD(4, "fuel_rod_quad");

    public final int size;
    public final String key;

    FuelRodType(int size, String key) {
        this.size = size;
        this.key = key;
    }
}