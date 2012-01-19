package pl.softwaremill.common.util.io;

import java.io.IOException;
import java.util.List;

public class KillableProcess {
    private final String shellCommand;
    private final String processGrepString;
    
    private List<String> pids;
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
        
        pids = Shell.readProcessPids(processGrepString);
        
        return process;
    }
    
    public void sendSigInt() throws IOException, InterruptedException {
        sendSig(1);
    }
    
    public void sendSigKill() throws IOException, InterruptedException {
        sendSig(9);
    }
    
    public void sendSig(int sig) throws IOException, InterruptedException {
        for (String pid : pids) {
            Shell.runShellCommand("kill -"+sig+" "+pid).waitFor();
        }
        
        process = null;
        pids = null;
    }

    public List<String> getPids() {
        return pids;
    }

    public Process getProcess() {
        return process;
    }
}
