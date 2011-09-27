package pl.softwaremill.common.test.web.soap;

import com.eviware.soapui.model.support.TestRunListenerAdapter;
import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
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
