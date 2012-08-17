package pl.softwaremill.common.util.tuples;

import com.google.common.base.Function;

import javax.annotation.Nullable;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class Pair<A, B> {
    private final A left;
    private final B right;

    protected Pair(A left, B right) {
        this.left = left;
        this.right = right;
    }

    public A getLeft() {
        return left;
    }

    public B getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;

        Pair pair = (Pair) o;

        if (left != null ? !left.equals(pair.left) : pair.left != null) return false;
        if (right != null ? !right.equals(pair.right) : pair.right != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    /**
     * Factory method instantiating a pair.
     *
     * @param left  the left-hand element
     * @param right the right-hand element
     * @param <A>   the type of the left-hand element
     * @param <B>   the type of the right-hand element
     * @return the constructed pair
     */
    public static <A, B> Pair<A, B> pair(A left, B right) {
        return new Pair<A, B>(left, right);
    }

    public static class Functions {
        private Functions() {
            /* this class should not be instantiated */
        }

        public static <L, R> Function<Pair<L, R>, L> getLeft() {
            return new Function<Pair<L, R>, L>() {
                @Override
                public L apply(@Nullable Pair<L, R> input) {
                    return input == null
                            ? null
                            : input.getLeft();
                }
            };
        }

        public static <L, R> Function<Pair<L, R>, R> getRight() {
            return new Function<Pair<L, R>, R>() {
                @Override
                public R apply(@Nullable Pair<L, R> input) {
                    return input == null
                            ? input.getRight()
                            : null;
                }
            };
        }
    }
}
