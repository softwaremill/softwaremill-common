package pl.softwaremill.common.cdi.autofactory.multipleconstructors;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;
import pl.softwaremill.common.arquillian.ManifestUtil;
import pl.softwaremill.common.cdi.autofactory.CreatedWith;
import pl.softwaremill.common.cdi.autofactory.Discounts;
import pl.softwaremill.common.cdi.autofactory.Product;

import javax.inject.Inject;

/**
 * @author Maciej Bilas
 * @since 16/4/12 12:58
 */
public class TwoValidConstructorsTest extends Arquillian {

    @Deployment
    public static JavaArchive createArchive() {
        JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addClass(BeanWithAdditionalDefaultConstructor.class)
                .addClass(BeanWithAdditionalDefaultConstructor.Factory.class)
                .addClass(Product.class)
                .addClass(Discounts.class)
                .addPackage(CreatedWith.class.getPackage());

        return ManifestUtil.addEmptyBeansXml(javaArchive);
    }

    @Inject
    private BeanWithAdditionalDefaultConstructor.Factory factory;

    @Test
    public void arqShouldConstructedThisClass() {
        /* Nothing needs doing here. Any errors will appear as exception during the construction phase */
    }
}
