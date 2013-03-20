package pl.softwaremill.common.test.web.soap;

import com.eviware.soapui.model.support.TestRunListenerAdapter;
import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStepResult;
import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Publish result of Failed soapUI test case as a TeamCity artifact
 */
public class ArtifactPublisherListener extends TestRunListenerAdapter {

    private final static Logger LOG = LoggerFactory.getLogger(ArtifactPublisherListener.class);

    @Override
    public void afterStep(TestCaseRunner testCaseRunner, TestCaseRunContext testCaseRunContext, TestStepResult testStepResult) {
        try {
            LOG.info("soapUI TestCase:TestStep [" + testCaseRunner.getTestCase().getLabel() + ":" +
                    testStepResult.getTestStep().getLabel() + "] finished with status [" + testStepResult.getStatus() + "]");
            if (isFailed(testCaseRunner, testStepResult)) {
                File file = createFile(testStepResult);
                publishArtifact(writeResultTo(testStepResult, file));
            }
        } catch (Exception e) {
            LOG.error("Error occurred during publishing artifact!", e);
        }
    }

    private File createFile(TestStepResult testStepResult) throws IOException {
        return File.createTempFile(createPrefix(testStepResult), createSuffix());
    }

    private boolean isFailed(TestCaseRunner testCaseRunner, TestStepResult testStepResult) {
        return TestStepResult.TestStepStatus.FAILED.equals(testStepResult.getStatus())
                || TestCaseRunner.Status.FAILED.equals(testCaseRunner.getStatus());
    }

    private File writeResultTo(TestStepResult testStepResult, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos, Charsets.UTF_8));
        testStepResult.writeTo(pw);
        pw.close();
        fos.close();
        return file;
    }

    private void publishArtifact(File f) {
        System.out.println("##teamcity[publishArtifacts '" + f.getAbsolutePath() + "']");
    }

    private String createSuffix() {
        return new SimpleDateFormat(".yyyy-MM-dd-HH-mm-ss").format(new Date()) + ".txt";
    }

    private String createPrefix(TestStepResult testStepResult) {
        return "sopaui_result_" + testStepResult.getTestStep().getLabel() + "_";
    }

}
