package pl.softwaremill.common.cdi.sysprops;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.math.BigDecimal;

import static com.google.common.base.Objects.equal;
import static java.lang.String.format;

/**
 *
 * @author Marcin Jekot
 * @author Maciej Bilas
 * @since 14/9/12 16:33
 */
public class SystemPropertyProducer {

    @Produces
    @SystemProperty
    public String produceStringSystemProperty(InjectionPoint ip) {
        SystemProperty configAnnotation = getConfigAnnotation(ip);
        String propertyName = getPropertyKeyName(configAnnotation);
        String propertyValue = System.getProperty(propertyName, getDefaultValue(configAnnotation));

        if (propertyValue == null) {
            throw new RuntimeException(format("Required system property '%s' is not set.", propertyName));
        }

        return propertyValue;
    }

    @Produces
    @SystemProperty
    public Boolean produceBooleanSystemProperty(InjectionPoint ip) {
        return Boolean.parseBoolean(produceStringSystemProperty(ip));
    }

    @Produces
    @SystemProperty
    public Integer produceIntegerSystemProperty(InjectionPoint ip) {
        try {
            return Integer.parseInt(produceStringSystemProperty(ip));
        } catch (NumberFormatException nfe) {
            throw parseException(ip, Integer.class);
        }
    }

    @Produces
    @SystemProperty
    public Double produceDoubleSystemProperty(InjectionPoint ip) {
        try {
            return Double.parseDouble(produceStringSystemProperty(ip));
        } catch (NumberFormatException nfe) {
            throw parseException(ip, Double.class);
        }
    }

    @Produces
    @SystemProperty
    public BigDecimal produceBigDecimalSystemProperty(InjectionPoint ip) {
        try {
            return new BigDecimal(produceStringSystemProperty(ip));
        } catch (NumberFormatException nfe) {
            throw parseException(ip, BigDecimal.class);
        }
    }

    private String getPropertyKeyName(SystemProperty config) {
        if (!equal(config.value(), SystemProperty.NOT_SET)) {
            return config.value();
        }
        if (!equal(config.key(), SystemProperty.NOT_SET)) {
            return config.key();
        }

        throw new RuntimeException(format("Incorrect usage of the @%s annotation," +
                " either key() or value() must be set", SystemProperty.class.getSimpleName()));
    }

    private String getDefaultValue(SystemProperty config) {
        return equal(config.defaultValue(), SystemProperty.NOT_SET)
                ? null
                : config.defaultValue();
    }

    private SystemProperty getConfigAnnotation(InjectionPoint ip) {
        return ip.getAnnotated().getAnnotation(SystemProperty.class);
    }

    private RuntimeException parseException(InjectionPoint ip, Class type) {
        String key = getPropertyKeyName(getConfigAnnotation(ip));
        String value = produceStringSystemProperty(ip);

        return new RuntimeException(
                format("System property '%s'='%s' is not a valid %s", key, value, type.getSimpleName()));
    }
}
