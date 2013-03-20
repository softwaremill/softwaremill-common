package pl.softwaremill.common.cdi.sysprops;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.softwaremill.common.cdi.sysprops.SystemProperty.NOT_SET;

/**
 * @author Maciej Bilas
 * @author Marcin Jekot
 * @since 14/9/12 16:46
 */
public class SystemPropertyProducerTest {

    private static final String TEST_PROPERTY = "SystemPropertyProducerTest.TEST_PROPERTY";

    private SystemPropertyProducer service = new SystemPropertyProducer();

    @BeforeMethod
    @AfterClass(alwaysRun = true)
    private void cleanupSystemProperty() {
        System.clearProperty(TEST_PROPERTY);
    }

    private void setTestProperty(String value) {
        System.setProperty(TEST_PROPERTY, value);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldThrowREIfNoConfigValueCanBeFound() {
        service.produceStringSystemProperty(mockInjectionPointForDefaultTestProperty());
    }

    @Test
    public void shouldReturnTheSystemPropertyValueWhenFound() {
        // Given
        setTestProperty("value");

        // When
        String propertyValue = service.produceStringSystemProperty(mockInjectionPointForDefaultTestProperty());

        // Then
        assertThat(propertyValue).isEqualTo("value");
    }

    @Test
    public void shouldFallbackToDefaultValueIfSystemPropertyIsNotFound() {
        // When
        String propertyValue = service.produceStringSystemProperty(
                mockInjectionPoint(TEST_PROPERTY, null, "default value"));

        // Then
        assertThat(propertyValue).isEqualTo("default value");
    }

    @Test
    public void shouldTreatKeyAndValueParametersInterchangeablyForPresentValues() {
        // Given
        setTestProperty("foo");

        // When
        String obtainedFromSystemPropertyViaValue = service.produceStringSystemProperty(
                mockInjectionPoint(TEST_PROPERTY, NOT_SET));
        String obtainedFromSystemPropertyViaKey = service.produceStringSystemProperty(
                mockInjectionPoint(TEST_PROPERTY, NOT_SET, NOT_SET));

        // Then
        assertThat(obtainedFromSystemPropertyViaKey).isEqualTo("foo");
        assertThat(obtainedFromSystemPropertyViaValue).isEqualTo("foo");
    }

    @Test
    public void shouldTreatKeyAndValueParametersInterchangeablyForDefaultValues() {
        // When
        String obtainedFromSystemPropertyViaValue = service.produceStringSystemProperty(
                mockInjectionPoint(TEST_PROPERTY, "foo"));
        String obtainedFromSystemPropertyViaKey = service.produceStringSystemProperty(
                mockInjectionPoint(TEST_PROPERTY, NOT_SET, "foo"));

        // Then
        assertThat(obtainedFromSystemPropertyViaKey).isEqualTo("foo");
        assertThat(obtainedFromSystemPropertyViaValue).isEqualTo("foo");
    }

    @Test
    public void shouldProcessBooleanAsAccordingToBooleanToBooleanMethod() {
        assertThat(service.produceBooleanSystemProperty(
                mockInjectionPoint(NOT_SET, TEST_PROPERTY, "true")
        )).isTrue();

        assertThat(service.produceBooleanSystemProperty(
                mockInjectionPoint(NOT_SET, TEST_PROPERTY, "false")
        )).isFalse();

        assertThat(service.produceBooleanSystemProperty(
                mockInjectionPoint(NOT_SET, TEST_PROPERTY, "f")
        )).isFalse();

        assertThat(service.produceBooleanSystemProperty(
                mockInjectionPoint(NOT_SET, TEST_PROPERTY, "yes") // Nope, it's not true
        )).isFalse();
    }

    @Test
    public void shouldRetrunBooleanValueFromSystemProperty() {
        // Given
        System.setProperty(TEST_PROPERTY, "true");

        // When, then
        assertThat(service
                .produceBooleanSystemProperty(
                        mockInjectionPoint(TEST_PROPERTY, NOT_SET, NOT_SET)))
                .isTrue();

    }

    @Test
    public void shouldThrowInformativeExceptionForInteger() {
        // Given
        System.setProperty(TEST_PROPERTY, "not a number");

        // When
        try {
            service.produceIntegerSystemProperty(mockInjectionPoint(NOT_SET,
                    TEST_PROPERTY, NOT_SET));
        } catch (RuntimeException re) {

            assertThat(re).hasMessage("System property '" + TEST_PROPERTY + "'='not a number' is not a valid Integer");
        }
    }

    @Test
    public void shouldThrowInformativeExceptionForDouble() {
        // Given
        System.setProperty(TEST_PROPERTY, "not a number");

        // When
        try {
            service.produceDoubleSystemProperty(mockInjectionPoint(NOT_SET,
                    TEST_PROPERTY, NOT_SET));
        } catch (RuntimeException re) {
            // Then
            assertThat(re).hasMessage("System property '" + TEST_PROPERTY + "'='not a number' is not a valid Double");
        }
    }

    @Test
    public void shouldThrowInformativeExceptionForBigDecimal() {
        // Given
        System.setProperty(TEST_PROPERTY, "not a number");

        // When
        try {
            service.produceBigDecimalSystemProperty(mockInjectionPoint(NOT_SET,
                    TEST_PROPERTY, NOT_SET));
        } catch (RuntimeException re) {
            // Then
            assertThat(re).hasMessage("System property '" + TEST_PROPERTY + "'='not a number' is not a valid BigDecimal");
        }
    }

    private InjectionPoint mockInjectionPoint(String value, String key, String defaultValue) {
        InjectionPoint injectionPoint = mock(InjectionPoint.class);
        Annotated annotated = mock(Annotated.class);
        SystemProperty config = mock(SystemProperty.class);
        when(injectionPoint.getAnnotated()).thenReturn(annotated);
        when(annotated.getAnnotation(SystemProperty.class)).thenReturn(config);
        when(config.value()).thenReturn(value);
        when(config.key()).thenReturn(key);
        when(config.defaultValue()).thenReturn(defaultValue);

        return injectionPoint;
    }

    private InjectionPoint mockInjectionPoint(String key, String defaultValue) {
        return mockInjectionPoint(NOT_SET, key, defaultValue);
    }

    private InjectionPoint mockInjectionPoint(String value) {
        return mockInjectionPoint(value, NOT_SET, NOT_SET);
    }

    private InjectionPoint mockInjectionPointForDefaultTestProperty() {
        return mockInjectionPoint(TEST_PROPERTY);
    }
}
