package pl.softwaremill.common.debug.timing;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;

/**
 * @link http://seamframework.org/Community/SeamPerformanceProblemRewardingWorkaround
 * @author Tobias Hill
 * @author Adam Warski
 */
@Interceptor
@Timed
public class TimingInterceptor implements Serializable {
    @AroundInvoke
    public Object timeCall(InvocationContext invocation) throws Exception {
        long t0 = System.nanoTime();
        try {
            return invocation.proceed();
        } finally {
            long dt = System.nanoTime() - t0;
            TimingResults.instance.addInvocation(invocation, dt);
        }
    }
}
