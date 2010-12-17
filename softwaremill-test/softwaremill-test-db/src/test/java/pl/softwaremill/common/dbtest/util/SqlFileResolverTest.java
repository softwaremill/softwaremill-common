package pl.softwaremill.common.dbtest.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pl.softwaremill.common.dbtest.AbstractDBTest;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Pawel Wrzeszcz (pawel . wrzeszcz [at] gmail . com)
 */
public class SqlFileResolverTest {
	
	@Test(dataProvider = "getClasses")
	public void testResolveSqlFile(Class clazz, String expectedPath) throws Exception {
		final String filePath = new SqlFileResolver(clazz).getSqlFilePath();
		assertThat(filePath).isEqualTo(expectedPath);
	}

	@DataProvider
	public Object[][] getClasses() {
	    return new Object[][] {
	        {AbstractDBTest.class, "pl/softwaremill/common/dbtest/AbstractDBTest.sql"},
	        {SqlFileResolverTest.class, "pl/softwaremill/common/dbtest/util/SqlFileResolverTest.sql"},
	    };
	} 
}
