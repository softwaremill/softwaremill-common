package com.softwaremill.common.test.web.selenium;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Maciej Bilas
 * @since 7/2/12 10:36
 */
public class ServerPropertiesTest {

    ServerProperties serverProperties() {
        return new ServerProperties("localhost");
    }
    
    @Test
    public void testingSettingAdditionalSystemPropertiesWithSingleProperty() {
        assertSystemPropertiesFromMapEqual(ImmutableMap.of("foo", "bar"), "-Dfoo=bar");
    }

    @Test
    public void testingSettingAdditionalSystemPropertiesWithTwoValues() {
        LinkedHashMap<String,String> props = Maps.newLinkedHashMap();
        props.put("foo", "bar");
        props.put("eggs", "bacon");

        assertSystemPropertiesFromMapEqual(props, "-Dfoo=bar -Deggs=bacon");
    }

    private void assertSystemPropertiesFromMapEqual(Map<String, String> propertyMap, String expected) {
        assertThat(serverProperties().additionalSystemPropertiesFrom(propertyMap).getAdditionalSystemProperties())
                .isEqualTo(expected);
    }

}
