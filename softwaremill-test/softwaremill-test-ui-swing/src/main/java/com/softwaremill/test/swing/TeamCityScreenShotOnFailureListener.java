package pl.softwaremill.test.swing;

import org.fest.swing.testng.listener.AbstractTestListener;
import org.testng.ITestResult;

class TeamCityScreenShotOnFailureListener extends AbstractTestListener {

    private GuiScreenShotTaker screenshot;

    TeamCityScreenShotOnFailureListener() {
        screenshot = new GuiScreenShotTaker();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        screenshot.take(result.getTestClass().getName());
    }

}
