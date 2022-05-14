package top.focess.mc.mi.nuclear.mi;

import java.util.function.BiPredicate;

public enum NuclearReactionType {
    SIMULATION_3X3(){
        @Override
        public BiPredicate<Integer, Integer> getReaction() {
            return (x, y) -> x >= 1 && x <= 3 && y >= 1 && y <= 3;
        }

        @Override
        public int getSize() {
            return 5;
        }
    }
    ;
    public abstract BiPredicate<Integer,Integer> getReaction();

    public abstract int getSize();
}
