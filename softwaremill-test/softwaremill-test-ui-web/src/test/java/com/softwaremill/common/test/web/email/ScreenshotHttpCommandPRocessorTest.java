package com.softwaremill.common.test.web.email;

import com.thoughtworks.selenium.CommandProcessor;
import org.mockito.Matchers;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.softwaremill.common.test.web.selenium.screenshots.ScreenshotHttpCommandProcessor;
import com.softwaremill.common.test.web.selenium.screenshots.Screenshotter;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Pawel Stawicki on Mar 19, 2011 10:40:55 AM
 */
public class ScreenshotHttpCommandPRocessorTest {

    private static final String VALID_COMMAND = "valid command";
    private static final String EXCEPTION_THROWING_COMMAND = "throw exception";

    private CommandProcessor parentProcessor;
    private ExceptionThrowingScreenshotter exceptionThrowingScreenshotter;
    private ScreenshotHttpCommandProcessor processor;

    @BeforeTest
    private void initializeMocks() {
        parentProcessor = mock(CommandProcessor.class);
        when(parentProcessor.doCommand(eq(VALID_COMMAND), Matchers.<String[]>any())).thenReturn(null);
        when(parentProcessor.doCommand(eq(EXCEPTION_THROWING_COMMAND), Matchers.<String[]>any()))
                .thenThrow(new RuntimeException());
        exceptionThrowingScreenshotter = new ExceptionThrowingScreenshotter();

        processor = new ScreenshotHttpCommandProcessor(parentProcessor, exceptionThrowingScreenshotter);
    }

    @BeforeMethod
    private void resetScreenshots() {
        exceptionThrowingScreenshotter.resetScreenshotsCount();
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void shouldNotFallIntoIniniteLoopIfCaptureScreenshotThrowsException() throws Exception {
        //when
        //Do some command that throws exception. Taking screenshot also throws exception, it should
        // not fall into infinite loop.
        exceptionThrowingScreenshotter.setExceptionThrowing(true);
        processor.doCommand(EXCEPTION_THROWING_COMMAND, null);
    }

    /*
        1. A command that throws exception
        2. Try to take screenshot, it also throws exception. Don't fall into infinite loop
        3. New command that throws exception
        4. Should take screenshot correctly
     */
    @Test
    public void shouldTakeScreenshotForCommandAfterThrowingExceptionWhileTakingScreenshot() throws Exception {
        //when
        //Do some command that throws exception. Taking screenshot also throws exception, it should
        // not fall into infinite loop. Screenshot not taken.
        try {
            exceptionThrowingScreenshotter.setExceptionThrowing(true);
            processor.doCommand(EXCEPTION_THROWING_COMMAND, null);
            Assert.fail("Exception should be thrown");
        } catch (RuntimeException e) {
            //It's ok. This exception was expected.
        }

        //then
        Assert.assertEquals(exceptionThrowingScreenshotter.screenshotsCount(), 0);

        //Now again issue "exceptional" command, but this time screenshot should be captured.
        try {
            exceptionThrowingScreenshotter.setExceptionThrowing(false);
            processor.doCommand(EXCEPTION_THROWING_COMMAND, null);
            Assert.fail("Exception should be thrown");
        } catch (RuntimeException e) {
            //It's ok. This exception was expected.
        }

        //then
        Assert.assertEquals(exceptionThrowingScreenshotter.screenshotsCount(), 1);
    }

    //Simulate situation where taking screenshot throws exception.
    private class ExceptionThrowingScreenshotter implements Screenshotter {
        private boolean throwException;
        private int screenshots;

        @Override
        public void doScreenshot() {
            if (throwException) {
                processor.doCommand(EXCEPTION_THROWING_COMMAND, null);
            } else {
                screenshots++;
            }
        }

        public void setExceptionThrowing(boolean throwException) {
            this.throwException = throwException;
        }

        public int screenshotsCount() {
            return screenshots;
        }

        public void resetScreenshotsCount() {
            screenshots = 0;
        }
    }
}
