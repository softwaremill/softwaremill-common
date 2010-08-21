package pl.softwaremill.common.util.io;

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * A wrapped {@link java.io.InputStream} providing additional method.
 * @author Adam Warski (adam at warski dot org)
 */
public class RichStream {
    private final InputStream inputStream;

    public RichStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    /**
     * Closes the wrapped stream.
     * @return The wrapped String's content as a String.
     */
    public String asString() {
        try {
            return CharStreams.toString(new InputStreamReader(inputStream));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }

    /**
     * Closes the wrapped stream.
     * @param other Stream to compare to.
     * @return True if the other stream is the same as the wrapped one.
     */
    public boolean equalTo(InputStream other) {
        try {
            return ByteStreams.equal(
                    new InputStreamInputSupplier(inputStream),
                    new InputStreamInputSupplier(other));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }

    public void close() {
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
