package com.softwaremill.common.dbtest.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.softwaremill.common.dbtest.AbstractDBTest;

import static org.assertj.core.api.Assertions.assertThat;

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
	        {AbstractDBTest.class, "com/softwaremill/common/dbtest/AbstractDBTest.sql"},
	        {SqlFileResolverTest.class, "com/softwaremill/common/dbtest/util/SqlFileResolverTest.sql"},
	    };
	} 
}
