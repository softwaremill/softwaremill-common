package com.softwaremill.common.test.util;

import com.dumbster.smtp.SimpleSmtpServer;

import java.util.concurrent.Semaphore;

/**
 * Use this instead of {@link com.dumbster.smtp.SimpleSmtpServer#start()}, as that implementation contains a
 * concurrency bug which may cause a lock: it may happen (and happens almost always on my machine) that notify()
 * is called before wait(), so then the wait() waits indifinetly.
 */
public class SimpleSmtpServerStarter {
    public static SimpleSmtpServer start(int port) {
        final SimpleSmtpServer server = new SimpleSmtpServer(port);

        final Semaphore waitingThreadEnteredSynchronized = new Semaphore(0);
        final Semaphore serverStarted = new Semaphore(0);

        // First we start a thread where we will wait for the server to startup. We only proceed after the new
        // thread entered the synchronized block, by waiting on a semaphore (waitingThreadEnteredSynchronized).
        new Thread() {
            @Override
            public void run() {
                // Block until the server socket is created
                synchronized (server) {
                    waitingThreadEnteredSynchronized.release();
                    try {
                        server.wait();
                    } catch (InterruptedException e) {
                        // Ignore don't care.
                    }
                    serverStarted.release();
                }
            }
        }.start();

        try {
            waitingThreadEnteredSynchronized.acquire(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Then we start the server thread
        Thread t = new Thread(server);
        t.start();

        // When the server is started, it calls notify, which in turn causes the serverStarted semaphore to be released.
        try {
            serverStarted.acquire(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return server;
    }
}
