package com.softwaremill.test.swing;

import org.fest.swing.annotation.GUITest;
import org.testng.annotations.Listeners;

@GUITest
@Listeners({TeamCityScreenShotOnFailureListener.class})
public class AbstractGUITest {

    private GuiScreenShotTaker screenShot;

    protected AbstractGUITest() {
        screenShot = new GuiScreenShotTaker();
    }

    protected void screenShot() {
        screenShot.take(getClass().getName());
    }

}
