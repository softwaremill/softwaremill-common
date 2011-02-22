package pl.softwaremill.common.debug.timing;

import java.lang.reflect.Method;

/**
 * TimedInvocation is an invocation (i.e. a method call) which is being
 * counted and timed.
 * @link http://seamframework.org/Community/SeamPerformanceProblemRewardingWorkaround
 * @author Tobias Hill
 * @author Adam Warski
 */
public class TimedInvocation implements Comparable<TimedInvocation> {

    private long dt;
    private int calls = 1;
    private Method method;

    public TimedInvocation(Method method, long dt) {
        this.method = method;
        this.dt = dt;
    }

    public String toString() {
        String className = method.getDeclaringClass().getName();
        String shortendName = className.substring(method.getDeclaringClass().getPackage().getName().length() + 1);
        String duration = String.format("%11.2f ms", dt / 1e6);
        String avgDuration = String.format("%11.2f ms", dt / (1e6 * calls));
        String nCallStr = String.format("%8d", calls);
        return duration + avgDuration + nCallStr + "   " + shortendName + "." + method.getName() + "()";
    }

    public void anotherCall(long dt) {
        this.dt += dt;
        calls++;
    }

    public int compareTo(TimedInvocation o) {
        return -Long.valueOf(dt).compareTo(o.dt);
    }
}
