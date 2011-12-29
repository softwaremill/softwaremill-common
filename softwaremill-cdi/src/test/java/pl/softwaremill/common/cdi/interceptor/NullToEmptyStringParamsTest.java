package pl.softwaremill.common.cdi.interceptor;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.testng.annotations.Test;

import javax.inject.Inject;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class NullToEmptyStringParamsTest extends Arquillian {
	@Deployment
	public static JavaArchive createTestArchive() {
		JavaArchive ar = ShrinkWrap.create(JavaArchive.class, "test.jar")
				.addClass(BeanToIntercept.class)
				.addClass(NullToEmptyStringParams.class)
				.addClass(NullToEmptyStringParamsInterceptor.class);

		ar = addTestBeansXml(ar);

		return ar;
	}

	@Inject
	private BeanToIntercept bean;

	@Test
	public void shouldConvertMethodParamsToEmptyStrings() throws Exception {
	    // When
		String[] result = bean.method(null, null);

		// Then
		assertThat(result).containsOnly("","");
	}

	@Test
	public void shouldNotChangeNonNulls() throws Exception {
	    // When
		String[] result = bean.method("not null", "me too");

		// Then
		assertThat(result).containsOnly("not null", "me too");
	}

	@Test
	public void shouldNotChangeNonStrings() throws Exception {
	    // When
		Object[] result = bean.method2(null, null);

		// Then
		assertThat(result).containsOnly(null,"");
	}

	private static <T extends Archive<T> & ResourceContainer<T>> T addTestBeansXml(T archive) {
		StringBuilder beansXmlBuilder = new StringBuilder();
		beansXmlBuilder.append("<beans>");
		beansXmlBuilder.append("<interceptors>");
		beansXmlBuilder.append("<class>pl.softwaremill.common.cdi.interceptor.NullToEmptyStringParamsInterceptor</class>");
		beansXmlBuilder.append("</interceptors>");
		beansXmlBuilder.append("</beans>");

		return archive.addResource(
				new ByteArrayAsset(beansXmlBuilder.toString().getBytes()),
				ArchivePaths.create("META-INF/beans.xml"));
	}
}