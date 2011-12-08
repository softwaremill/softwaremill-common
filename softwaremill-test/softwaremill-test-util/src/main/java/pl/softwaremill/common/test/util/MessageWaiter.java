package pl.softwaremill.common.test.util;

import java.util.Scanner;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class MessageWaiter {

	private final Process process;

	public MessageWaiter(Process process) {
		this.process = process;
	}

	public void waitFor(String message) {
		System.out.println("Waiting for message: [" + message + "]");
		final Scanner scanner = new Scanner(process.getInputStream()).useDelimiter(message);
		scanner.next();
	}

}
