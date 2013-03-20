package com.softwaremill.common.cdi.conf;

import com.softwaremill.common.conf.Configuration;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.util.Map;

public class ConfValueProducer {
    @Produces
    @ConfValue(confName = "", confKey = "")
    public String getConfigurationValue(InjectionPoint ip) {
        ConfValue confValue = ip.getAnnotated().getAnnotation(ConfValue.class);
        return Configuration.get(confValue.confName()).get(confValue.confKey());
    }
    
    public static String from(InjectionPoint ip, Map<String, String> configuration) {
        ConfValue confValue = ip.getAnnotated().getAnnotation(ConfValue.class);
        return configuration.get(confValue.confKey());
    }
}
