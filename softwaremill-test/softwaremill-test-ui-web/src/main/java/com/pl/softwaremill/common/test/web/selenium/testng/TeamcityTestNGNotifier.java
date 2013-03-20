package pl.softwaremill.common.test.web.selenium.testng;

import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Reports tests on the fly for TeamCity
 */
public class TeamcityTestNGNotifier extends TestListenerAdapter {

    public static final String TEST_STARTED = "##teamcity[testStarted name='%1$s' captureStandardOutput='true']";
    public static final String TEST_ENDED = "##teamcity[testFinished name='%1$s' duration='%2$s']";
    public static final String TEST_FAILED = "##teamcity[testFailed name='%1$s' message='%2$s' details='%3$s']";

    private static final Set<String> startedTests = new HashSet<String>();
    private static final Set<String> finishedTests = new HashSet<String>();

    private String[][] charsToReplace = new String[][]{
            new String[]{"|", "||"},
            new String[]{"'", "|'"},
            new String[]{"\n", "|n"},
            new String[]{"\r", "|r"},
            new String[]{"\u0085", "|x"},
            new String[]{"\u2028", "|l"},
            new String[]{"\u2029", "|p"},
            new String[]{"[", "|["},
            new String[]{"]", "|]"}
    };

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = getTestName(result);

        if (!finishedTests.contains(testName)) {
            finishedTests.add(testName);

            System.out.println(String.format(TEST_ENDED, escapeForTeamcity(testName),
                    result.getEndMillis() - result.getStartMillis()));
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = getTestName(result);

        if (!startedTests.contains(testName)) {
            startedTests.add(testName);
            System.out.println(String.format(TEST_STARTED, escapeForTeamcity(testName)));
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = getTestName(result);

        if (!finishedTests.contains(testName)) {
            finishedTests.add(testName);
            // get the stacktrace first
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            result.getThrowable().printStackTrace(pw);

            System.out.println(String.format(TEST_FAILED, escapeForTeamcity(testName),
                    escapeForTeamcity(result.getThrowable().getMessage()),
                    escapeForTeamcity(sw.toString())
            ));

            // and mark test finished
            onTestSuccess(result);
        }
    }

    private String getTestName(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getName();
    }

    private String escapeForTeamcity(String string) {
        // more info http://confluence.jetbrains.net/display/TCD65/Build+Script+Interaction+with+TeamCity#BuildScriptInteractionwithTeamCity-ReportingTests

        if (string != null) {
            for (String[] chars : charsToReplace) {
                string = string.replace(chars[0], chars[1]);
            }
        }

        return string;
    }
}
