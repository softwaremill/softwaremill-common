package com.softwaremill.common.util.io;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class KillableProcess {
    private static final Logger log = LoggerFactory.getLogger(KillableProcess.class);

    private final String shellCommand;
    private final String[] processGrepStrings;
    
    private Process process;

    /**
     * @param shellCommand Command to run the process.
     * @param processGrepStrings Strings which will be used when grepping the process list to determine the process's pid.
     */
    public KillableProcess(String shellCommand, String... processGrepStrings) {
        this.shellCommand = shellCommand;
        this.processGrepStrings = processGrepStrings;
    }
    
    public Process start() throws IOException {
        return startWithEnvironment(ImmutableMap.<String, String>of());
    }

    public Process startWithEnvironmentVar(String name, String value) throws IOException {
        return startWithEnvironment(ImmutableMap.of(name, value));
    }

    public Process startWithEnvironment(Map<String, String> env) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", shellCommand);
        processBuilder.environment().putAll(env);

        process = processBuilder.start();

        return process;
    }
    
    public void sendSigInt() throws IOException, InterruptedException {
        sendSig(2);
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
        return Shell.readProcessPids(processGrepStrings);
    }

    public Process getProcess() {
        return process;
    }
}
