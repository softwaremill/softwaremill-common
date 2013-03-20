package pl.softwaremill.common.cdi.sysprops;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * @author Maciej Bilas
 * @since 14/9/12 16:33
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, FIELD, PARAMETER, TYPE})
public @interface SystemProperty {
    public static final String NOT_SET = "[no default set]";

    @Nonbinding public String value() default NOT_SET;

    @Nonbinding public String key() default NOT_SET;

    @Nonbinding public String defaultValue() default NOT_SET;

}
