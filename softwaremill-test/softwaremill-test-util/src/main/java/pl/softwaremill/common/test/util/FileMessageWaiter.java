package pl.softwaremill.common.test.util;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileMessageWaiter {
    private final File file;

    public FileMessageWaiter(File file) {
        this.file = file;
    }
    
    public void waitFor(final String message, long waitForMillis) {
        final AtomicBoolean found = new AtomicBoolean();
        
        Tailer tailer = new Tailer(file, new TailerListenerAdapter() {
            private Tailer tailer;

            @Override
            public void init(Tailer tailer) {
                this.tailer = tailer;
                System.out.println("Waiting for message: [" + message + "]");
            }

            @Override
            public void handle(String line) {
                if (line.contains(message)) {
                    found.set(true);
                    tailer.stop();
                }
            }
        }, 500);

        Thread tailerThread = new Thread(tailer);
        tailerThread.start();
        try {
            tailerThread.join(waitForMillis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (!found.get()) {
            throw new RuntimeException("Message: "+message+" not found in file: "+file.getAbsolutePath()+" during "+
                    waitForMillis+"ms");
        }
    }
}
