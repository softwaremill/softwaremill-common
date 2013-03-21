# SoftwareMill Test Server

Provides server for testing.

You can easily start the server, setup it with Responders, send some requests and receive response.
There is LoggingAndRememberingResponder which logs received requests to console and stores data about them, so in your test you
can run component sending requests and later verify if the requests sent were as expected.

Setup is very easy all you need is an implementation of Responder interface. You can use provided one LogAndStoreRequestResponder.

Then the test looks like this:

    LogAndStoreRequestResponder responder = new LogAndStoreRequestResponder();
    TestServer server = new TestServer();
    
    server.addResponder(responder);
    server.start();

    // Run your code, connecting to the server and sending some requests. 
    // LogAndStoreRequestResponder responds always with "OK"     
            
    // Then verify your requests
    List<LogAndStoreRequestResponder.RequestInfo> requestsReceived = responder.getRequestsReceived();
    LogAndStoreRequestResponder.RequestInfo request = requestsReceived.get(0);

    assertThat(request.getMethod()).isEqualTo("GET");
    assertThat(request.getPath()).isEqualTo("/path");

    assertThat(request.getParameters().get("param1")).hasSize(1);
    assertThat(request.getParameters().get("param1")[0]).isEqualTo("value1");

    assertThat(request.getHeaders().size()).isGreaterThanOrEqualTo(1);
    assertThat(request.getHeaders().get("Custom-Header")).hasSize(1);
    assertThat(request.getHeaders().get("Custom-Header")[0]).isEqualTo("CustomValue");

    server.stop();

By default TestServer runs on port 18182 for HTTP, and 18183 for HTTPS, but it is configurable in TestServer constructor.

Look to tests for more examples of use.
