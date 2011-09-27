package pl.softwaremill.common.test.web.soap;

import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestRunListener;
import com.eviware.soapui.model.testsuite.TestRunner;
import com.eviware.soapui.model.testsuite.TestStep;
import com.eviware.soapui.model.testsuite.TestStepResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Publish result of Failed soapUI test case as a TeamCity artifact
 */
public class ArtifactPublisherListener implements TestRunListener {

    private final static Logger LOG = LoggerFactory.getLogger(ArtifactPublisherListener.class);

    @Override
    public void beforeRun(TestCaseRunner testCaseRunner, TestCaseRunContext testCaseRunContext) {
    }

    @Override
    public void afterRun(TestCaseRunner testCaseRunner, TestCaseRunContext testCaseRunContext) {
    }

    @Override
    public void beforeStep(TestCaseRunner testCaseRunner, TestCaseRunContext testCaseRunContext) {
    }

    @Override
    public void beforeStep(TestCaseRunner testCaseRunner, TestCaseRunContext testCaseRunContext, TestStep testStep) {
    }

    @Override
    public void afterStep(TestCaseRunner testCaseRunner, TestCaseRunContext testCaseRunContext, TestStepResult testStepResult) {
        try {
            LOG.info("soapUI TestCase:TestStep [" + testCaseRunner.getTestCase().getLabel() + ":" +
                    testStepResult.getTestStep().getLabel() +
                    "] finished with status [" + testCaseRunner.getStatus() + "]");
            if (TestRunner.Status.FAILED.equals(testCaseRunner.getStatus())) {
                String date = new SimpleDateFormat(".yyyy-MM-dd-HH-mm-ss.").format(new Date());
                File f = File.createTempFile("sopaui_result_" + testStepResult.getTestStep().getLabel() + "_", date + ".txt");
                FileOutputStream fos = new FileOutputStream(f);
                PrintWriter pw = new PrintWriter(fos);
                testStepResult.writeTo(pw);
                pw.close();
                fos.close();
                System.out.println("##teamcity[publishArtifacts '" + f.getAbsolutePath() + "']");
            }
        } catch (Exception e) {
            LOG.error("Error occurred during publishing artifact!", e);
        }
    }
}
