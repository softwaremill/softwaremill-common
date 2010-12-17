package pl.softwaremill.common.test.util;

import org.mockito.MockitoAnnotations;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

/**
 * Allows use of @Mock annotation in TestNG test.
 *
 * To make use of this listener declare it in the testng.xml file on using @Listeners annotation.
 *
 * Unfortunately, under IntelliJ IDEA you have to always declare listener in the run/debug configuration.
 *
 * @author Pawel Wrzeszcz (pawel . wrzeszcz [at] gmail . com)
 */
public class MockitoTestNGListener implements IInvokedMethodListener {

	@Override
	public void beforeInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
		Object[] instances = iInvokedMethod.getTestMethod().getInstances();
		for (Object instance : instances) {
			MockitoAnnotations.initMocks(instance);
		}
	}

	@Override
	public void afterInvocation(IInvokedMethod iInvokedMethod, ITestResult iTestResult) {
		// Empty
	}
}
