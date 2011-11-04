package pl.softwaremill.common.conf.encoding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class MasterPasswordStore {
    private static String masterPassword;

    public static String getMasterPassword() {
        if (masterPassword == null) {
            throw new IllegalStateException("Master password not set!");
        }

        return masterPassword;
    }

    public static void setMasterPassword(String masterPassword) {
        MasterPasswordStore.masterPassword = masterPassword;
    }

    public static void readFromStdIn() throws IOException {
        System.out.println("Master password:");
        masterPassword = new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
}
