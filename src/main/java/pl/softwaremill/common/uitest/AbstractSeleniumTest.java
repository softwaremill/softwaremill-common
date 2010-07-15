package pl.softwaremill.common.uitest;

import java.io.FileOutputStream;
import java.io.PrintStream;

import org.openqa.selenium.server.RemoteControlConfiguration;
import org.openqa.selenium.server.SeleniumServer;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

/**
 *
 * @author maciek
 */
public class AbstractSeleniumTest {
  
    private SeleniumServer server;
    
    protected Selenium selenium;

    private int seleniumServerPort;
    
    private String testServerPort;
    

    public AbstractSeleniumTest() throws Exception {
        System.out.println("--- AbstractSeleniumTest()");
        seleniumServerPort = Integer.parseInt(System.getProperty("selenium.server.port", "14444"));
        testServerPort = System.getProperty("selenium.testserver.port", "8080");
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
        setupBrowser(testServerPort);    
    }
    
    public void setupBrowser(String port) {
        System.out.println("--- Starting browser");

        selenium = new DefaultSelenium(
                "127.0.0.1", seleniumServerPort, "*firefox", "http://localhost:"+port);
        selenium.start();

        System.out.println("--- Started browser");
    }

    @AfterTest
    public void stopBrowser() {
        System.out.println("--- Stopping browser");
        selenium.stop();
        System.out.println("--- Stopped browser");
    }
    
}
