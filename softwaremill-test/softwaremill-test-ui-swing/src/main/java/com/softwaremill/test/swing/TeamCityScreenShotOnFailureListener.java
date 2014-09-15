package com.softwaremill.test.swing;

import org.assertj.swing.testng.listener.AbstractTestListener;
import org.testng.ITestNGListener;
import org.testng.ITestResult;

class TeamCityScreenShotOnFailureListener extends AbstractTestListener implements ITestNGListener {

    private GuiScreenShotTaker screenshot;

    TeamCityScreenShotOnFailureListener() {
        screenshot = new GuiScreenShotTaker();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        screenshot.take(result.getTestClass().getName());
    }

}
