package pl.softwaremill.common.util;

/**
 * Suspends the current thread for exponentially increasing intervals of time, until the maximum sleep interval is
 * reached.
 */
public class IncrementalSleeper {
    private final int initialInterval;
    private final int maxInterval;

    private int currentInterval;

    public IncrementalSleeper(int initialInterval, int maxInterval) {
        this.initialInterval = initialInterval;
        this.maxInterval = maxInterval;

        this.currentInterval = initialInterval;
    }

    public int getCurrentInterval() {
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
