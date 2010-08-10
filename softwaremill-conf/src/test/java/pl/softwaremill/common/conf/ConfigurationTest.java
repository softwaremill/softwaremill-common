package pl.softwaremill.common.conf;

import org.testng.annotations.Test;

import java.util.Properties;

import static org.fest.assertions.Assertions.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ConfigurationTest {
    @Test
    public void testTest1FromClasspath() {
        Properties props = Configuration.get("test1");
        assertThat(props.size()).isEqualTo(1);
        assertThat(props.get("a")).isEqualTo("b");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testTest2FromClasspath() {
        Configuration.get("test2");
    }
}
