package pl.softwaremill.common.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class MessageWaiter {

	private final InputStream inputStream;

	public MessageWaiter(Process process) {
		this.inputStream = process.getInputStream();
	}

	public MessageWaiter(File file) throws FileNotFoundException {
		this.inputStream = new FileInputStream(file);
	}

	public void waitFor(String message) {
		System.out.println("Waiting for message: [" + message + "]");
		final Scanner scanner = new Scanner(inputStream).useDelimiter(message);
		scanner.next();
	}

}
