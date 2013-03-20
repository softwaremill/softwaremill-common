package com.softwaremill.common.cdi.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class LoggerProducer {

    @Produces
    public Logger produceLog(InjectionPoint injectionPoint) {
        Bean<?> bean = injectionPoint.getBean();
        if (bean == null) {
            return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass());
        } else {
            return LoggerFactory.getLogger(bean.getBeanClass());
        }
    }

}
