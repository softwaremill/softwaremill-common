package pl.softwaremill.common.sqs;

import com.amazonaws.Protocol;
import com.google.common.base.Objects;
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

    static String serializeToBase64(Serializable object) throws IOException {
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

    static Object deserializeFromBase64(String data) throws IOException, ClassNotFoundException {
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

    static Protocol determineProtocol(String server) {
        checkNotNull(server);

        String[] splitServer = server.split(":");

        Protocol protocol = Protocol.HTTPS;
        if (splitServer.length > 1) {
            String port = splitServer[1];
            String protocolInTheUrl = splitServer[0].toLowerCase();
            if (splitServer[1].startsWith("//")) {
                if (Objects.equal(protocolInTheUrl, "http"))
                    return Protocol.HTTP;
                else if (Objects.equal(protocolInTheUrl, "https"))
                    return Protocol.HTTPS;
                port = splitServer[2];
            }
            if (!"443".equals(port)) {
                protocol = Protocol.HTTP;
            }
        }

        return protocol;
    }
}
