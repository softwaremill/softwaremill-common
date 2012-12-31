package pl.softwaremill.common.conf.encoding.server;

import com.google.common.base.Charsets;
import pl.softwaremill.common.conf.encoding.MasterPasswordStore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class MasterPasswordSetterServer implements Runnable {
    public static final int PORT = 39512;

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Bound server to port " + PORT);

            try {
                tryAcceptAndReadPassword(serverSocket);
            } finally {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Exception in master password listener server");
            e.printStackTrace();
        }
    }

    private void tryAcceptAndReadPassword(ServerSocket serverSocket) throws IOException {
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected");

        try {
            readPassword(clientSocket);
        } finally {
            clientSocket.close();
        }
    }

    private void readPassword(Socket clientSocket) throws IOException {
        String password = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), Charsets.UTF_8)).readLine();
        MasterPasswordStore.setMasterPassword(password);
        System.out.println("Master password read and set successfully");
    }
}
