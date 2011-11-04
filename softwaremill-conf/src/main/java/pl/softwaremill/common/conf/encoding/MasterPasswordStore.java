package pl.softwaremill.common.conf.encoding;

import java.io.IOException;

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

    public static void readFromConsole() throws IOException {
        masterPassword = new String(System.console().readPassword("Master password: "));
    }
}
