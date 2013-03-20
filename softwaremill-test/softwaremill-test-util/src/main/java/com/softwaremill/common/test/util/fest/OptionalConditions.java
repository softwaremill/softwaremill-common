package com.softwaremill.common.test.util.fest;

import com.google.common.base.Optional;
import org.fest.assertions.Condition;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Maciej Bilas
 * @since 13/9/12 12:07
 */
public class OptionalConditions {

    private static final Condition<Object> PRESENT_CONDITION = new Condition<Object>() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public boolean matches(Object o) {
            if (o == null)
                return false;

            checkArgument(o instanceof Optional);

            return ((Optional<?>) o).isPresent();
        }
    };

    private static final Condition<Object> ABSENT_CONDITION = new Condition<Object>() {
        @SuppressWarnings("ConstantConditions")
        @Override
        public boolean matches(Object o) {
            if (o == null)
                return false;

            checkArgument(o instanceof Optional);

            return !((Optional) o).isPresent();
        }
    };

    public static Condition<Object> present() {
        return PRESENT_CONDITION;
    }

    public static Condition<Object> absent() {
        return ABSENT_CONDITION;
    }
}
