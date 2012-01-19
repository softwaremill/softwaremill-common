package pl.softwaremill.common.util.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static com.google.common.io.CharStreams.*;

public class Shell {
    public static Process runShellCommand(String command) throws IOException {
        return Runtime.getRuntime().exec(new String[] { "sh", "-c", command });
    }

    public static List<String> readProcessPids(String processGrepString) throws IOException {
        Process getPidProcess = runShellCommand("ps ax | grep " + processGrepString + " | grep -v grep | cut -c1-5");
        return readLines(newReaderSupplier(
                new InputStreamInputSupplier(getPidProcess.getInputStream()),
                Charset.defaultCharset()));
    }
}
