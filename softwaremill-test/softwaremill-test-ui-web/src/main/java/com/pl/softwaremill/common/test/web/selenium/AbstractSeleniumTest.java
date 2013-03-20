package pl.softwaremill.common.test.web.selenium;

import com.google.common.base.Charsets;
import com.thoughtworks.selenium.Selenium;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import pl.softwaremill.common.test.web.selenium.screenshots.FailureTestListener;
import pl.softwaremill.common.test.web.selenium.screenshots.Screenshotter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author maciek
 */
@Listeners({FailureTestListener.class})
public abstract class AbstractSeleniumTest {

	private static SeleniumServer server;

	public static Selenium selenium;

	private static SeleniumBrowserProperties browserProperties;

	private static String seleniumHost;
	private static int seleniumServerPort;

    private static boolean debug;

    private static int seleniumTimeout;

    static {
        seleniumHost = System.getProperty("selenium.server", "127.0.0.1");
        seleniumServerPort = Integer.parseInt(System.getProperty("selenium.server.port", "14444"));
		String testServerPort = System.getProperty("selenium.testserver.port", "8280");
		String testServerUrl = System.getProperty("selenium.testserver.url", "http://localhost");
		String browserCommand = System.getProperty("selenium.browser.command", "*firefox");
        debug = Boolean.parseBoolean(System.getProperty("selenium.debug.enabled", "true"));

        seleniumTimeout = Integer.parseInt(System.getProperty("selenium.timeout", "120"));

        browserProperties = new SeleniumBrowserProperties(browserCommand, testServerUrl, testServerPort);
    }

	public AbstractSeleniumTest() {

	}

	/**
	 * Returns object with properties for the browser started by selenium, including base url.
	 * This can be used to modify this properties, e.g. when using different test url.
	 */
	public SeleniumBrowserProperties getBrowserProperties() {
        return browserProperties;
	}

	@BeforeSuite
	public void setupSelenium() throws Exception {

		System.out.println("--- Starting selenium server with debug set to " + debug);

		RemoteControlConfiguration rcc = new RemoteControlConfiguration();
		rcc.setTimeoutInSeconds(seleniumTimeout);
		rcc.setPort(seleniumServerPort);
		//rcc.setMultiWindow(true);
		rcc.setSingleWindow(false);
		PrintStream ps = System.out; // backup
		System.setOut(new PrintStream(new FileOutputStream("logfile"), false, Charsets.UTF_8.name()));
		rcc.setDebugMode(debug);
		server = new SeleniumServer(false, rcc);
		System.setOut(ps); // restore

		server.start();
		System.out.println("--- Started selenium server");
	}

	@AfterSuite(alwaysRun = true)
	public void stopSelenium() throws Exception {
		System.out.println("--- Stopping selenium server");
		server.stop();
		System.out.println("--- Stopped selenium server");
	}

	@BeforeTest
	public void setupBrowser() throws Exception {
		String url = browserProperties.getBrowserURL();
		if (browserProperties.getBrowserPort() != null) {
			url += ":" + browserProperties.getBrowserPort();
		}
		System.out.println("--- Starting browser on url: " + url);

		selenium = new SMLSelenium(
				seleniumHost,
				seleniumServerPort,
				browserProperties.getBrowserCommand(),
				url,
				new Screenshotter() {
					@Override
					public void doScreenshot() {
						try {
							// make a screenshot

							captureScreenshot();
						} catch (Exception e) {
							// shouldn't happen
							throw new RuntimeException(e);
						}
					}
				});
		selenium.start();

		System.out.println("--- Started browser");

		// perform optional logic
		afterSeleniumStart();
	}

	@AfterTest
	public void stopBrowser() throws Exception {
		beforeSeleniumStop();

		System.out.println("--- Stopping browser");
		selenium.stop();
		System.out.println("--- Stopped browser");
	}

    /**
     * @deprecated Please use regular TestNG asserts
     *
     * @param var
     * @throws Exception
     */
    @Deprecated
	public static void assertTrue(boolean var) throws Exception {
		if (!var) {
			captureScreenshot();
		}

		Assert.assertTrue(var);
	}

    /**
     * @deprecated Please use regular TestNG asserts
     *
     * @param o1
     * @param o2
     * @throws Exception
     */
    @Deprecated
	public static void assertEquals(Object o1, Object o2) throws Exception {
		if (!o1.equals(o2)) {
			captureScreenshot();
		}

		Assert.assertEquals(o1, o2);
	}

    /**
     * @deprecated Please use regular TestNG asserts
     *
     * @param message
     * @throws Exception
     */
    @Deprecated
	public static void fail(String message) throws Exception {
		captureScreenshot();

		Assert.fail(message);
	}

    /**
     * Use {@link #captureScreenshot(String)} instead.
     */
    @Deprecated
    public static File captureScreenshot() throws Exception {
        return captureScreenshot("");
    }

	public static File captureScreenshot(String testName) throws Exception {
        File f = createTempArtifactFile("selenium_screenshot_", testName, ".png");

		selenium.captureEntirePageScreenshot(f.getAbsolutePath(), "");

        publishArtifact(f);

        return f;
	}

    public static File captureHtmlSource(String testName) throws Exception {
        File f = createTempArtifactFile("selenium_source_", testName, ".txt");

        String htmlSource = selenium.getHtmlSource();
        FileUtils.write(f, htmlSource);

        publishArtifact(f);

        return f;
    }

    private static void publishArtifact(File f) {
        System.out.println("##teamcity[publishArtifacts '" + f.getAbsolutePath() + "']");
    }

    private static File createTempArtifactFile(String prefix, String testName, String suffix)
            throws IOException {
        String date = new SimpleDateFormat(".yyyy-MM-dd-HH-mm-ss.").format(new Date());
        return File.createTempFile(prefix + testName + "_", date + "failed" + suffix);
    }

	protected void afterSeleniumStart() throws Exception {
		// base implementation does nothing
	}

	protected void beforeSeleniumStop() throws Exception {
		// base implementation does nothing
	}

	@Deprecated // Use SeleniumCommands.clickAndWait instead
	public static void clickAndWait(String locator, String timeout) {
		SeleniumCommands.clickAndWait(locator, timeout);
	}

}
