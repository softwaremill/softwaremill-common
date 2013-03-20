package pl.softwaremill.common.cdi.objectservice.extension;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * A {@code ?} wildcard.
 * @author Adam Warski (adam at warski dot org)
 */
public class PureWildcardType implements WildcardType {
    private static final Type[] EMPTY_TYPE_ARRAY = new Type[0];

    public static final PureWildcardType INSTANCE = new PureWildcardType();

    private PureWildcardType() {
    }

    @Override
    public Type[] getUpperBounds() {
        return EMPTY_TYPE_ARRAY;
    }

    @Override
    public Type[] getLowerBounds() {
        return EMPTY_TYPE_ARRAY;
    }

    @Override
    public String toString() {
        return "?";
    }
}
