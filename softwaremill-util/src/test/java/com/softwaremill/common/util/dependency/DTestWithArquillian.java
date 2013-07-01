package com.softwaremill.common.util.dependency;

import com.softwaremill.common.arquillian.BetterArquillian;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import static org.fest.assertions.Assertions.assertThat;

public class DTestWithArquillian extends BetterArquillian {

    @Inject
    private BeanManager beanManager;

    @Deployment
    public static WebArchive deployment() {
        WebArchive javaArchive = ShrinkWrap.create(WebArchive.class);
        javaArchive.addClasses(RequestScopedService.class).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return javaArchive;
    }

    static class TestRunnable implements Runnable {

        RequestScopedService requestScopedService;

        RequestScopedService getRequestScopedService() {
            return requestScopedService;
        }

        @Override
        public void run() {
            requestScopedService = D.inject(RequestScopedService.class);
        }
    }

    @BeforeMethod
    public void register() {
        D.register(new BeanManagerDependencyProvider(beanManager));
    }

    @Test
    public void shouldExecuteInRequestContext() throws Exception {
        final TestRunnable runnable = new TestRunnable();
        D.inRequestContext(runnable);
        assertThat(runnable.getRequestScopedService()).isNotNull();

    }

}
