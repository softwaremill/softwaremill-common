package pl.softwaremill.common.cdi.autofactory.logger;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;
import pl.softwaremill.common.arquillian.ManifestUtil;
import pl.softwaremill.common.cdi.autofactory.CreatedWith;
import pl.softwaremill.common.cdi.logger.LoggerProducer;

import javax.inject.Inject;

import static org.fest.assertions.Assertions.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class TestInjectDataAndLogger extends Arquillian {
    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClass(LoggerProducer.class)
                .addClass(InjectDataAndLoggerConstructor.class)
                .addClass(InjectDataAndLoggerField.class)
                .addPackage(CreatedWith.class.getPackage());

        return ManifestUtil.addEmptyBeansXml(ar);
    }

    @Inject
    private InjectDataAndLoggerConstructor.Factory injectDataAndLoggerConstructorFactory;

    @Inject
    private InjectDataAndLoggerField.Factory injectDataAndLoggerFieldFactory;

    @Test
    public void testField() {
        assertThat(injectDataAndLoggerFieldFactory.create("z").getData()).isEqualTo("z");
        assertThat(injectDataAndLoggerFieldFactory.create("z").getLogger()).isNotNull();
    }

    @Test
    public void testConstructor() {
        assertThat(injectDataAndLoggerConstructorFactory.create("z").getData()).isEqualTo("z");
        assertThat(injectDataAndLoggerConstructorFactory.create("z").getLogger()).isNotNull();
    }
}
