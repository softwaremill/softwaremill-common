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
}
