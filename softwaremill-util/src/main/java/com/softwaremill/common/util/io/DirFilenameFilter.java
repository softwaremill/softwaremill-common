package pl.softwaremill.common.util.io;

import java.io.File;
import java.io.FilenameFilter;

/**
 * A filename filter which accepts only directories.
 * @author Adam Warski (adam at warski dot org)
 */
public class DirFilenameFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
        return new RichFile(dir).createChildFile(name).isDirectory();
    }
}
