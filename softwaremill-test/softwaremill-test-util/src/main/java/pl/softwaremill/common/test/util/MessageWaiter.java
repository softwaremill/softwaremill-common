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
		this(process.getInputStream());
	}

    /**
     * BROKEN! Will only search through file content as it is *now*.
     * Use {@link FileMessageWaiter} instead.
     */
	public MessageWaiter(File file) throws FileNotFoundException {
		this(new FileInputStream(file));
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
