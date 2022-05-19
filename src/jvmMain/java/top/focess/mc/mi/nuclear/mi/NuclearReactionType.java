package top.focess.mc.mi.nuclear.mi;

import top.focess.util.serialize.FocessSerializable;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public enum NuclearReactionType implements FocessSerializable {




    SIMULATION_3X3() {
        @Override
        public BiPredicate<Integer, Integer> getReaction() {
            return (x, y) -> GRID_LAYOUT[0][x][y] != null && GRID_LAYOUT[0][x][y];
        }

        @Override
        public int getSize() {
            return 5;
        }
    },

    SIMULATION_5X5() {
        @Override
        public BiPredicate<Integer, Integer> getReaction() {
            return (x, y) -> GRID_LAYOUT[1][x][y] != null && GRID_LAYOUT[1][x][y];
        }

        @Override
        public int getSize() {
            return 7;
        }
    },

    SIMULATION_7X7() {
        @Override
        public BiPredicate<Integer, Integer> getReaction() {
            return (x, y) -> GRID_LAYOUT[2][x][y] != null && GRID_LAYOUT[2][x][y];
        }

        @Override
        public int getSize() {
            return 9;
        }
    },

    SIMULATION_9X9() {
        @Override
        public BiPredicate<Integer, Integer> getReaction() {
            return (x, y) -> GRID_LAYOUT[3][x][y] != null && GRID_LAYOUT[3][x][y];
        }

        @Override
        public int getSize() {
            return 11;
        }
    }
    ;

    public abstract BiPredicate<Integer, Integer> getReaction();

    public Predicate<Integer> getLimitation() {
        return x -> x >= 1 && x < this.getSize() - 1;
    }

    public abstract int getSize();

    private static final Boolean[][][] GRID_LAYOUT;

    static {
        GRID_LAYOUT = new Boolean[4][][];

        for (int i = 0; i < 4; i++) {
            GRID_LAYOUT[i] = new Boolean[5 + 2 * i][5 + 2 * i];
            for (int x = -2 - i; x <= 2 + i; x++) {
                int minZ;
                int xAbs = Math.abs(x);
                if (i != 3)
                    if (xAbs == 0) minZ = 0; else minZ = xAbs - 1;
                else if (xAbs <= 1) minZ = 0; else minZ = xAbs - 2;

                int maxZ = 2 * (2 + i) - minZ;

                for (int z = minZ; z <= maxZ; z++)
                    GRID_LAYOUT[i][2 + i + x][z] = !(z == minZ || z == maxZ || xAbs == 2 + i);
            }
        }
    }
}
