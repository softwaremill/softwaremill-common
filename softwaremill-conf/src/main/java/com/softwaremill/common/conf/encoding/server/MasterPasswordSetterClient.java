package com.softwaremill.common.conf.encoding.server;

import com.google.common.base.Charsets;

import java.io.IOException;
import java.io.OutputStreamWriter;
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
        new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), Charsets.UTF_8), true).write(password);

        System.out.println("Password sent");

        socket.close();
    }
}
