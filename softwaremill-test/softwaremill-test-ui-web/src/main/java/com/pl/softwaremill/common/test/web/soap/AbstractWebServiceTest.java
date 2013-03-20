package com.softwaremill.common.test.web.soap;

import com.eviware.soapui.impl.wsdl.WsdlProject;
import com.eviware.soapui.model.support.PropertiesMap;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestRunner;
import com.eviware.soapui.model.testsuite.TestSuite;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Base class used to create web service test base on soapUI Test Suites
 */
public class AbstractWebServiceTest {

    private ArtifactPublisherListener listener;

    public AbstractWebServiceTest() {
        listener = new ArtifactPublisherListener();
    }

    /**
     * Runs all the tests defined in the given soapUI Test Suite,
     *
     * @param soapUITestSuite specify it like for example: System.getProperty("resources.dir") + "/src/test/resources/SoapUITestSuite.xml"
     * @throws Exception any exception
     */
    protected void run(String soapUITestSuite) throws Exception {
        run(new WsdlProject(soapUITestSuite));
    }

    /**
     * Runs SoapUI TestCase project
     *
     * @param project new WsdlProject("/path/to/soapui/testcase.xml")
     */
    protected void run(WsdlProject project) {
        for (TestSuite testSuite : project.getTestSuiteList()) {
            run(testSuite);
        }
    }

    /**
     * Runs all test cases in given SoapUI TestSuite
     *
     * @param testSuite SoapUI TestSuite
     */
    protected void run(TestSuite testSuite) {
        run(testSuite.getTestCaseList());
    }

    /**
     * Runs specified list of TestCases one by one
     *
     * @param testCases list of TestCases
     */
    protected void run(List<TestCase> testCases) {
        for (TestCase testCase : testCases) {
            run(testCase);
        }
    }

    /**
     * Runs one soapUI TestCase
     *
     * @param testCase single TestCase
     */
    protected void run(TestCase testCase) {
        testCase.addTestRunListener(listener);
        TestRunner runner = testCase.run(new PropertiesMap(), false);
        assertThat(runner.getStatus())
                .describedAs("TestCase [" + testCase.getLabel() + "] failed because of [" + runner.getReason() + "]")
                .isEqualTo(TestRunner.Status.FINISHED);
    }

}
