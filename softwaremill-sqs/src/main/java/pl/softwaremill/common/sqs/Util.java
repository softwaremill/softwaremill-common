package pl.softwaremill.common.sqs;

import pl.softwaremill.common.sqs.util.Base64Coder;

import java.io.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Maciej Bilas
 * @since 11/10/12 17:49
 */
class Util { /* Package-private visibility, do not escalate, refactor if you need to reuse it */

    private Util() {
        /* this class should not be instantiated */
    }

    public static String serializeToBase64(Serializable object) throws IOException {
        checkNotNull(object);

        ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = null;
        try {
            objectStream = new ObjectOutputStream(byteArrayStream);
            objectStream.writeObject(object);
            return new String(Base64Coder.encode(byteArrayStream.toByteArray()));
        } finally {
            if (objectStream != null)
                objectStream.close();
        }
    }

    public static Object deserializeFromBase64(String data) throws IOException, ClassNotFoundException {
        checkNotNull(data);

        ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(Base64Coder.decode(data));

        ObjectInputStream objectStream = null;
        try {
            objectStream = new ObjectInputStream(byteArrayStream);
            return objectStream.readObject();
        } finally {
            if (objectStream != null)
                objectStream.close();
        }
    }
}
