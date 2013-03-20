package com.softwaremill.common.util.io;

import com.google.common.io.InputSupplier;

import java.io.IOException;
import java.io.InputStream;

/**
 * An input supplier which holds a reference to an input stream.
 * @author Adam Warski (adam at warski dot org)
 */
public class InputStreamInputSupplier implements InputSupplier<InputStream> {
    private final InputStream stream;

    public InputStreamInputSupplier(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public InputStream getInput() throws IOException {
        return stream;
    }
}
