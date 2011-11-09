package pl.softwaremill.common.conf.encoding.server;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Usage:
 * java StartupPasswordListenerMainWrapper com.foo.Bar x y z
 *
 * First starts the master password listener server, and then invokes the main method of the specified class with
 * the specified arguments.
 *
 * @author Adam Warski (adam at warski dot org)
 */
public class StartupPasswordListenerMainWrapper {
    public static void main(String[] args) throws Exception {
        runMasterPasswordListenerServer();
        runSpecifiedMain(args);
    }

    private static void runMasterPasswordListenerServer() {
        Thread serverThread = new Thread(new MasterPasswordSetterServer());
        serverThread.setDaemon(true);
        serverThread.start();
    }

    private static void runSpecifiedMain(String[] args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String mainClassName = args[0];
        Class<?> mainClass = Thread.currentThread().getContextClassLoader().loadClass(mainClassName);

        // The first element is the name of the main class
        String[] argsFromSecondElement = Arrays.copyOfRange(args, 1, args.length, args.getClass());

        mainClass.getMethod("main", args.getClass()).invoke(null, (Object) argsFromSecondElement);
    }
}
