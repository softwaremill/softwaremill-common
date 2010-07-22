package pl.softwaremill.common.uitest;

import com.thoughtworks.selenium.Selenium;
import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 *
 * @author maciek
 */
public abstract class AbstractSeleniumTest {
  
    private SeleniumServer server;
    
    protected Selenium selenium;
    
    private SeleniumBrowserProperties browserProperties;

    private String seleniumHost;
    private int seleniumServerPort;    

    public AbstractSeleniumTest() {
        System.out.println("--- AbstractSeleniumTest()");
        seleniumHost = System.getProperty("selenium.server", "127.0.0.1");
        seleniumServerPort = Integer.parseInt(System.getProperty("selenium.server.port", "14444"));
        String testServerPort = System.getProperty("selenium.testserver.port", "8280");
        
        browserProperties = new SeleniumBrowserProperties("*firefox", "http://localhost", testServerPort);
    }

    @BeforeSuite
    public void setupSelenium() throws Exception {
        
        System.out.println("--- Starting selenium server");

        RemoteControlConfiguration rcc = new RemoteControlConfiguration();
        rcc.setTimeoutInSeconds(60);
        rcc.setPort(seleniumServerPort);
        //rcc.setMultiWindow(true);
        rcc.setSingleWindow(false);
        PrintStream ps = System.out; // backup
        System.setOut(new PrintStream(new FileOutputStream("logfile")));
        //SeleniumServer.setDebugMode(true);
        rcc.setDebugMode(true);
        server = new SeleniumServer(false, rcc);
        System.setOut(ps); // restore

        server.start();
        System.out.println("--- Started selenium server");
    }
    
    @AfterSuite(alwaysRun = true)
    public void stopSelenium() throws Exception, InterruptedException {
        System.out.println("--- Stopping selenium server");
        server.stop();
        System.out.println("--- Stopped selenium server");    
    }

    @BeforeTest
    public void setupBrowser() {
        String url = browserProperties.getBrowserURL();
        if(browserProperties.getBrowserPort() != null) {
            url += ":"+browserProperties.getBrowserPort();
        }
        System.out.println("--- Starting browser on url: "+url);
        
        selenium = new SMLSelenium(
                seleniumHost,
                seleniumServerPort,
                browserProperties.getBrowserCommand(),
                url,
                new Screenshotter(){
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
    }

    @AfterTest
    public void stopBrowser() {
        System.out.println("--- Stopping browser");
        selenium.stop();
        System.out.println("--- Stopped browser");
    }

    protected void assertTrue(boolean var) throws Exception {
        if (!var) {
            captureScreenshot();
        }

        Assert.assertTrue(var);
    }

    protected void assertEquals(Object o1, Object o2) throws Exception {
        if (!o1.equals(o2)) {
            captureScreenshot();
        }

        Assert.assertEquals(o1, o2);
    }

    protected void fail(String message) throws Exception {
        captureScreenshot();

        Assert.fail(message);
    }

    private void captureScreenshot() throws Exception {
        File f = File.createTempFile("selenium_screenshot", "failed.png");

        selenium.captureEntirePageScreenshot(f.getAbsolutePath(), "");

        System.out.println("##teamcity[publishArtifacts '"+f.getAbsolutePath()+"']");
    }
    
}
