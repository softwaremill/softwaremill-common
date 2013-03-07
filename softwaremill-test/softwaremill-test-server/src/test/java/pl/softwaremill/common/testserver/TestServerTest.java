package pl.softwaremill.common.testserver;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@Test(singleThreaded = true)
public class TestServerTest {

    @Test
     public void shouldStoreRequest() throws Exception {
        // Given
        TestServer server = new TestServer();
        server.start();

        try {
            LoggingAndRememberingResponder responder = new LoggingAndRememberingResponder();
            server.addResponder(responder);

            // When
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://localhost:18182/path?param1=value1&param2=2");
            get.addHeader("Custom-Header", "CustomValue");
            HttpResponse response = client.execute(get);

            // Then
            assertResponseOk(response);

            List<LoggingAndRememberingResponder.RequestInfo> requestsReceived = responder.getRequestsReceived();
            assertThat(requestsReceived).hasSize(1);
            LoggingAndRememberingResponder.RequestInfo request = requestsReceived.get(0);

            assertThat(request.getMethod()).isEqualTo("GET");

            assertThat(request.getPath()).isEqualTo("/path");

            assertThat(request.getParameters()).hasSize(2);
            assertThat(request.getParameters().get("param1")).hasSize(1);
            assertThat(request.getParameters().get("param1")[0]).isEqualTo("value1");
            assertThat(request.getParameters().get("param2")).hasSize(1);
            assertThat(request.getParameters().get("param2")[0]).isEqualTo("2");

            assertThat(request.getHeaders().size()).isGreaterThanOrEqualTo(1);
            assertThat(request.getHeaders().get("Custom-Header")).hasSize(1);
            assertThat(request.getHeaders().get("Custom-Header")[0]).isEqualTo("CustomValue");
        } finally {
            server.stop();
        }
    }

    @Test
    public void shouldStorePostRequest() throws Exception {
        // Given
        TestServer server = new TestServer();
        server.start();

        try {
            LoggingAndRememberingResponder responder = new LoggingAndRememberingResponder();
            server.addResponder(responder);

            // When
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://localhost:18182/path?param1=value1&param2=2");
            post.addHeader("Custom-Header", "CustomValue");

            BasicHttpEntity entity = new BasicHttpEntity();
            entity.setContent(IOUtils.toInputStream("some content sent in POST"));
            post.setEntity(entity);

            HttpResponse response = client.execute(post);

            // Then
            assertResponseOk(response);

            List<LoggingAndRememberingResponder.RequestInfo> requestsReceived = responder.getRequestsReceived();
            assertThat(requestsReceived).hasSize(1);
            LoggingAndRememberingResponder.RequestInfo request = requestsReceived.get(0);

            assertThat(request.getMethod()).isEqualTo("POST");

            assertThat(request.getPath()).isEqualTo("/path");

            assertThat(request.getParameters()).hasSize(2);
            assertThat(request.getParameters().get("param1")).hasSize(1);
            assertThat(request.getParameters().get("param1")[0]).isEqualTo("value1");
            assertThat(request.getParameters().get("param2")).hasSize(1);
            assertThat(request.getParameters().get("param2")[0]).isEqualTo("2");

            assertThat(request.getHeaders().size()).isGreaterThanOrEqualTo(1);
            assertThat(request.getHeaders().get("Custom-Header")).hasSize(1);
            assertThat(request.getHeaders().get("Custom-Header")[0]).isEqualTo("CustomValue");

            assertThat(request.getContent()).isEqualTo("some content sent in POST");
        } finally {
            server.stop();
        }
    }

    @Test
    public void shouldAcceptHttpsConnections() throws Exception {
        // Given
        TestServer server = new TestServer();
        addResponderAndStartServer(server);

        try {
            // When
            HttpResponse response = gulliblyCallHttps("https://localhost:18183", 18183);

            // Then
            assertResponseOk(response);
        } finally {
            server.stop();
        }
    }

    @Test
    public void shouldRunOnSpecifiedPort() throws Exception {
        // Given
        TestServer server = new TestServer(8765);
        addResponderAndStartServer(server);

        try {
            // When
            HttpResponse response = callUrl("http://localhost:8765");

            // Then
            assertResponseOk(response);
        } finally {
            server.stop();
        }
    }

    @Test
    public void shouldRunHttpsOnSpecifiedPort() throws Exception {
        // Given
        TestServer server = new TestServer(8765, 8766);
        addResponderAndStartServer(server);

        try {
            // When
            HttpResponse response = gulliblyCallHttps("https://localhost:8766", 8766);

            // Then
            assertResponseOk(response);
        } finally {
            server.stop();
        }
    }

    @Test
    public void shouldNotAllowUnauthenticatedUserOnSecuredPath() throws Exception {
        // Given
        TestServer server = new TestServer();
        addResponderAndStartServer(server);
        server.secure("/secure", "user", "pass");

        try {
            // When
            HttpResponse response = callUrl("http://localhost:18182/secure");

            // Then
            assertThat(response.getStatusLine().getStatusCode()).isEqualTo(401);
        } finally {
            server.stop();
        }
    }

    @Test
    public void shouldAllowAuthenticatedUserOnSecuredPath() throws Exception {
        // Given
        TestServer server = new TestServer();
        addResponderAndStartServer(server);
        server.secure("/secure", "user", "pass");

        try {
            // When
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://localhost:18182/secure");
            client.getCredentialsProvider().setCredentials(
                    new AuthScope("localhost", 18182),
                    new UsernamePasswordCredentials("user", "pass"));

            HttpResponse response = client.execute(get);

            // Then
            assertResponseOk(response);
        } finally {
            server.stop();
        }
    }

    @Test
    public void shouldAllowUnauthenticatedUserOnPublicPath() throws Exception {
        // Given
        TestServer server = new TestServer();
        addResponderAndStartServer(server);
        server.secure("/secure", "user", "pass");

        try {
            // When
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet("http://localhost:18182/public");
            client.getCredentialsProvider().setCredentials(
                    new AuthScope("localhost", 18182),
                    new UsernamePasswordCredentials("user", "pass"));

            HttpResponse response = client.execute(get);

            // Then
            assertResponseOk(response);
        } finally {
            server.stop();
        }
    }

    private HttpResponse callUrl(String url) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        return client.execute(get);
    }

    private HttpResponse gulliblyCallHttps(String url, int port) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        HttpClient client = createGullibleHttpClient(port);
        HttpGet get = new HttpGet(url);
        return client.execute(get);
    }

    private void assertResponseOk(HttpResponse response) throws IOException {
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(IOUtils.toString(response.getEntity().getContent())).isEqualTo("OK");
    }

    private void addResponderAndStartServer(TestServer server) throws Exception {
        server.addResponder(new LoggingAndRememberingResponder());
        server.start();
    }

    private HttpClient createGullibleHttpClient(int sslPort) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        SchemeRegistry registry = createTrustEveryoneRegistry(sslPort);
        ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager(registry);

        return new DefaultHttpClient(connectionManager);
    }

    private SchemeRegistry createTrustEveryoneRegistry(int sslPort) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        TrustStrategy easyStrategy = new TrustStrategy() {

            @Override
            public boolean isTrusted(X509Certificate[] certificate, String authType)
                    throws CertificateException {
                return true;
            }
        };

        SSLSocketFactory sf = new SSLSocketFactory(easyStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("https", sslPort, sf));

        return registry;
    }

}
