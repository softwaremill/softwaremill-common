package com.softwaremill.test.swing;

import org.assertj.swing.image.ImageException;
import org.assertj.swing.image.ScreenshotTaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class GuiScreenShotTaker {

    private static final Logger LOG = LoggerFactory.getLogger(GuiScreenShotTaker.class);

    private ScreenshotTaker screenshotTaker;
    private FileNameBuilder builder;

    GuiScreenShotTaker() {
        try {
            screenshotTaker = new ScreenshotTaker();
            builder = new FileNameBuilder();
        } catch (ImageException e) {
            LOG.error("Unable to create ScreenshotTaker", e);
        }

    }

    void take(String className) {
        try {
            tryTakeScreenShot(className);
        } catch (Exception e) {
            LOG.error("Unable to take screen shot!", e);
        }
    }

    private void tryTakeScreenShot(String className) {
        if (readyToTakeScreenShot()) {
            String fileName = builder.createTmpFileName(className);
            screenshotTaker.saveDesktopAsPng(fileName);
            System.out.println("##teamcity[publishArtifacts '" + fileName + "']");
        }
    }

    private boolean readyToTakeScreenShot() {
        return screenshotTaker != null && builder != null;
    }

}
