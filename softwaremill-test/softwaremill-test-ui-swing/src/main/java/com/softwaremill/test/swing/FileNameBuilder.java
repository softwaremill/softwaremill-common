package pl.softwaremill.test.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Random;

class FileNameBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(FileNameBuilder.class);

    private Random random;

    public FileNameBuilder() {
        random = new Random();
    }

    String createTmpFileName(String className) {
        String tmpFolder = readTmpDirPath();
        String prefix = className;
        if (className.indexOf(".") != -1) {
            prefix = className.substring(className.lastIndexOf(".") + 1);
        }
        String fileName = tmpFolder + prefix + "-" + random.nextInt(Integer.MAX_VALUE) + ".png";
        LOG.debug("Created temporary file name [" + fileName + "]");
        return fileName;
    }

    private String readTmpDirPath() {
        String tmp = System.getProperty("java.io.tmpdir");
        if (tmp == null) {
            tmp = File.separator;
        }
        if (tmp.endsWith(File.separator) == false) {
            tmp = tmp + File.separator;
        }
        return tmp;
    }

}
