package com.softwaremill.common.test.util;

import org.mockito.Mock;
import org.mockito.internal.util.MockUtil;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.softwaremill.common.test.util.MockitoTestNGListener;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Pawel Wrzeszcz (pawel . wrzeszcz [at] gmail . com)
 */
@Listeners(MockitoTestNGListener.class)
public class MockitoTestNGListenerTest {

	@Mock
	private Object mock;

	@Test
	public void shouldCreateMock() throws Exception {
	    assertThat(new MockUtil().isMock(mock)).isTrue();
	}

	private Object mockFromPreviousMethod;		

	@Test
	public void shouldCreateNewMockForEachTestMethod_Part1() throws Exception {
		mockFromPreviousMethod = mock;
	}

	@Test(dependsOnMethods = "shouldCreateNewMockForEachTestMethod_Part1")
	public void shouldCreateNewMockForEachTestMethod_Part2() throws Exception {
		assertThat(mockFromPreviousMethod).isNotEqualTo(mock);
	}
}
