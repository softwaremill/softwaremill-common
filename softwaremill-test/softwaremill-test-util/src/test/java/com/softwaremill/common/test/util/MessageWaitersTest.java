package com.softwaremill.common.test.util;

import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicLong;

import static org.fest.assertions.Assertions.*;

public class MessageWaitersTest {
    @Test
    public void testWaitForMessageInFile() throws IOException, InterruptedException {
        final File file = File.createTempFile("messagewaiter", "txt");

        try {
            PrintWriter writer = new PrintWriter(new FileOutputStream(file));

            doTest(writer, new Runnable() {
                @Override
                public void run() {
                    new FileMessageWaiter(file).waitFor("8", 5000L);
                }
            });
        } finally {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }

    @Test
    public void testWaitForMessageInStream() throws IOException, InterruptedException {
        PipedOutputStream output = new PipedOutputStream();
        final PipedInputStream input = new PipedInputStream(output);

        PrintWriter writer = new PrintWriter(output);

        doTest(writer, new Runnable() {
            @Override
            public void run() {
                new MessageWaiter(input).waitFor("8");
            }
        });
    }

    private void doTest(PrintWriter writer, final Runnable doWait)
            throws IOException, InterruptedException {
        // Given
        String text1 = "line 1\t\nline 2\t\nline 3\t\n";
        String text2 = "line 4\t\nline 5\t\nline 6\t\n";
        String text3 = "line 7\t\nline 8\t\nline 9\t\n";

        final AtomicLong waitingStarted = new AtomicLong();
        final AtomicLong waitingEnded = new AtomicLong();

        // When
        // Starting a thread which will count how long it waits for the message
        Thread waiter = new Thread(new Runnable() {
            @Override
            public void run() {
                waitingStarted.set(System.currentTimeMillis());
                doWait.run();
                waitingEnded.set(System.currentTimeMillis());
            }
        });
        waiter.start();

        // Writing some text, waiting, and then the desired text after a pause
        writer.write(text1);
        writer.write(text2);
        writer.flush();

        Thread.sleep(2000);

        writer.write(text3);
        writer.flush();

        waiter.join(10000);

        // Then
        assertThat(waitingEnded.get() - waitingStarted.get()).isGreaterThan(1000);
    }
}
