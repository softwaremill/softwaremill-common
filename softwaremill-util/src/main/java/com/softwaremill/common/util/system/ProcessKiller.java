package pl.softwaremill.common.util.system;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author Adam Warski (adam at warski dot org)
 */
public class ProcessKiller {
    /**
     * Tries to kill the given process. Will send kill -9 on UNIX. On Windows uses process.destroy().
     * @param process The process to kill.
     */
    public void kill(Process process) {
        // Hack: Getting the PID.
        if (process.getClass().getName().contains("UNIX")) {
            try {
                Field pidField = process.getClass().getDeclaredField("pid");
                pidField.setAccessible(true);
                int pid = (Integer) pidField.get(process);
                Runtime.getRuntime().exec("kill -9 " + pid);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Fall-back for non-windows systems
            process.destroy();
        }
    }
}
