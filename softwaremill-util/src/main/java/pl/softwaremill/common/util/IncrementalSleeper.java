package pl.softwaremill.common.util;

/**
 * Suspends the current thread for exponentially increasing intervals of time, until the maximum sleep interval is
 * reached.
 */
public class IncrementalSleeper {
    private final long initialInterval;
    private final long maxInterval;

    private long currentInterval;

    public IncrementalSleeper(long initialInterval, long maxInterval) {
        this.initialInterval = initialInterval;
        this.maxInterval = maxInterval;

        this.currentInterval = initialInterval;
    }

    public long getCurrentInterval() {
        return currentInterval;
    }

    public void sleep() {
        Sleeper.sleepFor(currentInterval);
        if (currentInterval < maxInterval) {
            currentInterval *= 2;
        }
    }

    public void reset() {
        currentInterval = initialInterval;
    }
}
