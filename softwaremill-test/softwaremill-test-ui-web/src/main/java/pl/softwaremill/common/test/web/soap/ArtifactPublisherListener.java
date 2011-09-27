package pl.softwaremill.common.test.web.soap;

import com.eviware.soapui.model.support.TestRunListenerAdapter;
import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestStepResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
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
            Throwable error = testStepResult.getError();
            if (error != null) {
                LOG.error("Error occurred during running TestCase:TestStep [" + testCaseRunner.getTestCase().getLabel() + ":" +
                        testStepResult.getTestStep().getLabel() + "]", error);
                File file = File.createTempFile(createPrefix(testStepResult), createSuffix());
                publishArtifact(writeResultTo(testStepResult, file));
            }
        } catch (Exception e) {
            LOG.error("Error occurred during publishing artifact!", e);
        }
    }

    private File writeResultTo(TestStepResult testStepResult, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        PrintWriter pw = new PrintWriter(fos);
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
