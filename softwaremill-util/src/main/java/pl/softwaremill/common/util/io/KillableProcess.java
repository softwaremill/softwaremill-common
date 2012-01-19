package pl.softwaremill.common.util.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class KillableProcess {
    private static final Logger log = LoggerFactory.getLogger(KillableProcess.class);

    private final String shellCommand;
    private final String processGrepString;
    
    private Process process;

    /**
     * @param shellCommand Command to run the process.
     * @param processGrepString String which will be used when grepping the process list to determine the process's pid.
     */
    public KillableProcess(String shellCommand, String processGrepString) {
        this.shellCommand = shellCommand;
        this.processGrepString = processGrepString;
    }
    
    public Process start() throws IOException {
        process = new ProcessBuilder("sh",
                "-c",
                shellCommand).start();
        
        return process;
    }
    
    public void sendSigInt() throws IOException, InterruptedException {
        sendSig(1);
    }
    
    public void sendSigKill() throws IOException, InterruptedException {
        sendSig(9);
    }

    public void sendSigTerm() throws IOException, InterruptedException {
        sendSig(15);
    }
    
    public void sendSig(int sig) throws IOException, InterruptedException {
        for (String pid : readPids()) {
            log.info("Sending signal {} to pid {}", sig, pid);
            Shell.runShellCommand("kill -"+sig+" "+pid).waitFor();
        }
        
        process = null;
    }

    public List<String> readPids() throws IOException {
        return Shell.readProcessPids(processGrepString);
    }

    public Process getProcess() {
        return process;
    }
}
