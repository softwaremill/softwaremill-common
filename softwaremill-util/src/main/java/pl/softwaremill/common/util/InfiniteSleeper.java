package pl.softwaremill.common.util;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class InfiniteSleeper {

	public synchronized void sleepInfinitely() throws InterruptedException {
		wait();
	}
}
