package pl.softwaremill.common.cdi.autofactory.instance;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;
import pl.softwaremill.common.cdi.autofactory.CreatedWith;
import pl.softwaremill.common.cdi.util.ArquillianUtil;

import javax.inject.Inject;

import static org.fest.assertions.Assertions.*;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class TestInjectDataAndInstance extends Arquillian {
    @Deployment
    public static JavaArchive createTestArchive() {
        JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClass(InjectDataAndInstanceConstructor.class)
                .addClass(InstanceBean.class)
                .addClass(InstanceBean2.class)
                .addPackage(CreatedWith.class.getPackage());

        ar = ArquillianUtil.addEmptyBeansXml(ar);

        return ar;
    }

    @Inject
    private InjectDataAndInstanceConstructor.Factory injectDataAndInstanceConstructorFactory;

    @Test
    public void testConstructor() {
        assertThat(injectDataAndInstanceConstructorFactory.create("z").getData()).isEqualTo("zab");
    }
}
