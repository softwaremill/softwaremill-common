package pl.softwaremill.common.conf;

import org.testng.annotations.Test;

import java.util.Map;

import static org.fest.assertions.Assertions.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ConfigurationTest {
    @Test
    public void testTest1FromClasspath() {
        Map<String, String> props = Configuration.get("test1");
        assertThat(props.size()).isEqualTo(2);
        assertThat(props.get("a")).isEqualTo("b");
        assertThat(props.get("c")).isEqualTo("d");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testTest2FromClasspath() {
        Configuration.get("test2");
    }

    @Test
    public void shouldGetDefaultValue() {
        Config<String, String> config = Configuration.get("test1");
        String value = config.get("nonexistingkey", "default value");
        assertThat(value).isEqualTo("default value");
}

    @Test
    public void shouldGetDefaultIntValue() {
        Config<String, String> config = Configuration.get("test1");
        int value = config.getAsInt("nonexistingkey", 2);
        assertThat(value).isEqualTo(2);
    }

    @Test
    public void shouldGetDefaultDoubleValue() {
        Config<String, String> config = Configuration.get("test1");
        double value = config.getAsDouble("nonexistingkey", 5.5);
        assertThat(value).isEqualTo(5.5);
    }

    @Test
    public void shouldGetDefaultBooleanValue() {
        Config<String, String> config = Configuration.get("test1");
        boolean value = config.getAsBoolean("nonexistingkey", false);
        assertThat(value).isEqualTo(false);
    }

    @Test
    public void shouldGetDefaultLongValue() {
        Config<String, String> config = Configuration.get("test1");
        long value = config.getAsLong("nonexistingkey", 0x7fffffffffffL);
        assertThat(value).isEqualTo(0x7fffffffffffL);
    }
}
