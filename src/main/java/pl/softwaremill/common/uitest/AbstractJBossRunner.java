package pl.softwaremill.common.uitest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 *
 * @author maciek
 */
public class AbstractJBossRunner {

    private String serverHome;
    private String serverPort;
    private String configuration;
    private boolean running;
    
    Process jbossProcess;
    
    /**
     * Assumes 'default' configuration
     * 
     * @param serverHome
     * @param serverPort
     * @param running  Whether the server is running and should not be started/stopped 
     */
    public AbstractJBossRunner(String serverHome, String serverPort, boolean running) {
        this.serverHome = serverHome;
        this.serverPort = serverPort;
        this.running = running;
        this.configuration = "default";
    }
    
    public AbstractJBossRunner(ServerPoperties serverPoperties) {
        this.serverHome = serverPoperties.getServerHome();
        this.serverPort = ""+serverPoperties.getPort();
        this.configuration = serverPoperties.getConfiguration();
        this.running =serverPoperties.isRunning();
    }
    
    
    public String getServerHome() {
        return serverHome;
    }

    public String getServerPort() {
        return serverPort;
    }
    
    public String getConfiguration() {
        return configuration;
    }
    
    public boolean isRunning() {
        return running;
    }

    
    public void deployFile(File from, File to, String message) {
        try {
            InputStream in = new FileInputStream(from);

            //For Overwrite the file.
            OutputStream out = new FileOutputStream(to);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            if (message != null) {
                System.out.println(message);  
            }            
        }
        catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + " in the specified directory.");
            System.exit(0);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }        
    }
    
    public void deployFile(File from, File to) {
        deployFile(from, to, null);
    }    
    
    public void undeploy(File file, String name) throws Exception {
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("--- " + name + " deleted");
            } else {
                throw new Exception("Couldn't delete " + name + " !");
            }
        } else {
            System.out.println("--- " + name + " was not present");
        }
    }
    
//    public void undeploy(File file) throws Exception {
//        undeploy(file, file.getName());
//    }
    
    public void undeploy(File... files) throws Exception {
        for (File file : files) {
            undeploy(file, file.getName());
        }        
    }   
    
    public Scanner start(Scanner scanner, String scannerDelimeter) throws Exception {
        if (!running) {
            System.out.println("--- Starting JBoss server");

            jbossProcess = Runtime.getRuntime().exec(new String[]{serverHome + "/bin/run.sh", "-c", configuration, "-Djboss.service.binding.set=ports-02"});
            serverPort = ""+Integer.valueOf(serverPort) +200;
            
            System.out.println("--- Process started, waiting for input: ["+scannerDelimeter+"]");
            
            scanner = new Scanner(jbossProcess.getInputStream()).useDelimiter(scannerDelimeter);
            
            scanner.next();

            System.out.println("--- JBoss server started");
        } else {
            System.out.println("--- JBoss Already Started");
            Process tailProcess = Runtime.getRuntime().exec(new String[]{"tail", "-f", serverHome + "/server/" + configuration + "/log/server.log"});

            // in this case read the log
            scanner = new Scanner(tailProcess.getInputStream());
        }
        
        return scanner;
    }
    
    /**
     * New Scanner and waiting for "Started in"
     */
    public Scanner start() throws Exception {
        return start(new Scanner(""), "Started in");
    }
    
    public void shutdown() throws Exception {
        if (!running) {
            System.out.println("--- Stopping JBoss server");
            Process shutdownProcess = Runtime.getRuntime().exec(new String[]{serverHome + "/bin/shutdown.sh", "-s", "localhost:1299", "-S"});
            // wait for shutdown to finish
            shutdownProcess.waitFor();
            System.out.println("--- JBoss Server stopped");
        } else {
            System.out.println("--- Not stopping JBoss");
        }
    }
}
