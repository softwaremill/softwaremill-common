package pl.softwaremill.common.util.io;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import static com.google.common.io.CharStreams.*;

public class Shell {
    public static Process runShellCommand(String command) throws IOException {
        return Runtime.getRuntime().exec(new String[] { "sh", "-c", command });
    }

    public static List<String> readProcessPids(String... processGrepStrings) throws IOException {
        StringBuilder cmd = new StringBuilder("ps ax");
        for (String processGrepString : processGrepStrings) {
            cmd.append(" | grep ").append(processGrepString);
        }
        cmd.append(" | grep -v grep | cut -c1-5");

        Process getPidProcess = runShellCommand(cmd.toString());
        return readLines(newReaderSupplier(
                new InputStreamInputSupplier(getPidProcess.getInputStream()),
                Charset.defaultCharset()));
    }
}
