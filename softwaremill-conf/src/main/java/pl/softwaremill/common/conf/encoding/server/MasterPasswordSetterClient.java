package pl.softwaremill.common.conf.encoding.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class MasterPasswordSetterClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", MasterPasswordSetterServer.PORT);

        System.out.println("Please enter the master password:");
        String password = System.console().readLine();

        new PrintWriter(socket.getOutputStream(), true).write(password);

        System.out.println("Password sent");

        socket.close();
    }
}
