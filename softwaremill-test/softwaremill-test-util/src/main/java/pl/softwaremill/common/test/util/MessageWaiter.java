package pl.softwaremill.common.test.util;

import java.io.InputStream;
import java.util.Scanner;

/**
 * <strong>Warning:</strong> do not use with {@code FileInputStream}. If you want to tail a file and wait for a message,
 * use {@link FileMessageWaiter}.
 * @author Pawel Wrzeszcz (pawel [at] softwaremill . com)
 */
public class MessageWaiter {
	private final InputStream inputStream;

	public MessageWaiter(Process process) {
		this(process.getInputStream());
	}

    public MessageWaiter(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public void waitFor(String message) {
		System.out.println("Waiting for message: [" + message + "]");
		final Scanner scanner = new Scanner(inputStream).useDelimiter(message);
		scanner.next();
	}

}
