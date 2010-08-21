package pl.softwaremill.common.util.io;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * A wrapper for {@link java.io.File} providing additional methods.
 * @author Adam Warski (adam at warski dot org)
 */
public class RichFile {
    private final File file;

    public RichFile(File file) {
        this.file = file;
    }

    public void writeStringToFile(String content) {
        try {
            Files.write(content, file, getCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param stream The stream to be written to the file. Closes the stream.
     */
    public void writeInputStream(final InputStream stream) {
        try {
            Files.copy(new InputStreamInputSupplier(stream), file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            new RichStream(stream).close();
        }
    }

    public String getStringFromFile() {
        try {
            return Files.toString(file, getCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Charset getCharset() {
        return Charset.forName("UTF-8");
    }

    /**
     * Recursively delete the directory denoted by the wrapped file.
     * @return True if the directory was successfully deleted.
     */
    public boolean deleteDirectory() {
        return deleteDirectory(file);
    }
    
    /**
     * Delete the file denoted by the wrapped file.
     * @return True if the file was successfully deleted.
     */
    public boolean deleteFile() {
        if (file.exists()) {
            if (file.isDirectory()) {
                return false;
            } else {
                return file.delete();
            }            
        }
        return false;
    }

    private boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    if (!file.delete()) {
                        return false;
                    }
                }
            }

            return path.delete();
        } else {
            return true;
        }
    }

    /**
     * @param childName Name of the child element.
     * @return File corresponding to the child element in the given directory, that is {@code dirFullPath/childName}.
     */
    public File createChildFile(String childName) {
        return new File(file.getAbsolutePath() + File.separator + childName);
    }
}
