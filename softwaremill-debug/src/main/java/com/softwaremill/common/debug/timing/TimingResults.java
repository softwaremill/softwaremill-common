package pl.softwaremill.common.debug.timing;

import javax.interceptor.InvocationContext;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TimingResults extends ThreadLocal<Map<Method, TimedInvocation>> {
    public final static TimingResults instance = new TimingResults();

    @Override
    protected Map<Method, TimedInvocation> initialValue() {
        return new HashMap<Method, TimedInvocation>();
    }

    public void addInvocation(InvocationContext invocation, long dt) {
        addInvocation(invocation.getMethod(), dt);
    }

    public void addInvocation(Method method, long dt) {
        Map<Method, TimedInvocation> invocations = get();
        if (!invocations.containsKey(method)) {
            invocations.put(method, new TimedInvocation(method, dt));
        } else {
            TimedInvocation timedInvocation = invocations.get(method);
            timedInvocation.anotherCall(dt);
        }
    }

    public void dumpCurrent() {
        ArrayList<TimedInvocation> sortedInvocations = new ArrayList<TimedInvocation>(get().values());
        Collections.sort(sortedInvocations);

        if (sortedInvocations.size() > 0) {
            System.out.println("---");
            for (TimedInvocation sortedInvocation : sortedInvocations) {
                System.out.println(sortedInvocation);
            }
        }

        get().clear();
    }
}
