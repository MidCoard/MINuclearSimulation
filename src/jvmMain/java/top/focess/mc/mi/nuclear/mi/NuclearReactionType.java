package top.focess.mc.mi.nuclear.mi;

import top.focess.util.serialize.FocessSerializable;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public enum NuclearReactionType implements FocessSerializable {
    SIMULATION_3X3(){


        @Override
        public BiPredicate<Integer, Integer> getReaction() {
            return (x, y) -> x >= 1 && x <= 3 && y >= 1 && y <= 3;
        }

        @Override
        public Predicate<Integer> getColumn() {
            return x -> x >= 1 && x <= 3;
        }

        @Override
        public int getSize() {
            return 5;
        }
    }
    ;
    public abstract BiPredicate<Integer,Integer> getReaction();

    public abstract Predicate<Integer> getColumn();

    public abstract int getSize();
}
