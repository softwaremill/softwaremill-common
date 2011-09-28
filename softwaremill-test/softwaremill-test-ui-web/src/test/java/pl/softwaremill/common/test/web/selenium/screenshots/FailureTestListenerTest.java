package pl.softwaremill.common.test.web.selenium.screenshots;

import com.thoughtworks.selenium.Selenium;
import org.testng.IClass;
import org.testng.ITestResult;
import org.testng.annotations.Test;
import pl.softwaremill.common.test.web.selenium.AbstractSeleniumTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class FailureTestListenerTest {
    
    @Test
    public void shouldMakeScreenshot() throws Exception {
        // given
        FailureTestListener listener = new FailureTestListener();

        AbstractSeleniumTest.selenium = mock(Selenium.class);

        ITestResult result = setupTestResult(AnSeleniumTest.class);

        ByteArrayOutputStream baos = setupSystemOut();

        // when
        listener.onTestFailure(result);
    
        // then
        assertThat(baos.toString()).contains("##teamcity[publishArtifacts '");
        verify(AbstractSeleniumTest.selenium).captureEntirePageScreenshot(anyString(), anyString());
    }

    @Test
    public void shouldNotMakeScreentshot() throws Exception {
        // given
        FailureTestListener listener = new FailureTestListener();

        AbstractSeleniumTest.selenium = mock(Selenium.class);

        ITestResult result = setupTestResult(FailureTestListenerTest.class);

        ByteArrayOutputStream baos = setupSystemOut();

        // when
        listener.onTestFailure(result);

        // then
        assertThat(baos.toString()).isEqualTo("");
        verifyZeroInteractions(AbstractSeleniumTest.selenium);
    }

    private ITestResult setupTestResult(Class value) {
        ITestResult result = mock(ITestResult.class);
        IClass testClass = mock(IClass.class);
        when(testClass.getRealClass()).thenReturn(value);
        when(result.getTestClass()).thenReturn(testClass);
        return result;
    }

    private ByteArrayOutputStream setupSystemOut() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(baos);
        System.setOut(out);
        return baos;
    }
}

class AnSeleniumTest extends AbstractSeleniumTest {}