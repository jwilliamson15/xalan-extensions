package com.example.xalan.extensions;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpXmlExtensionTest {

  private HttpServer server;
  private final AtomicReference<String> lastAcceptHeader = new AtomicReference<>();
  private final AtomicReference<String> lastTraceHeader = new AtomicReference<>();

  @AfterEach
  void tearDown() {
    if (server != null) {
      server.stop(0);
    }
  }

  @Test
  void getReturnsResponseBodyForSuccessfulRequest() throws IOException {
    server = startServer(200, "ok");
    String url = "http://localhost:" + server.getAddress().getPort() + "/test";

    HttpXmlExtension extension = new HttpXmlExtension();

    assertEquals("ok", extension.get(url));
  }

  @Test
  void getThrowsForNonSuccessfulStatus() throws IOException {
    server = startServer(500, "error");
    String url = "http://localhost:" + server.getAddress().getPort() + "/test";

    HttpXmlExtension extension = new HttpXmlExtension();

    RuntimeException ex = assertThrows(RuntimeException.class, () -> extension.get(url));
    assertTrue(ex.getMessage().contains("status 500"));
  }

  @Test
  void getWithHeadersObjectSendsAllHeaders() throws IOException {
    server = startServer(200, "ok");
    String url = "http://localhost:" + server.getAddress().getPort() + "/test";

    HttpXmlExtension extension = new HttpXmlExtension();
    HttpRequestHeaders headers = extension.newHeaders();
    extension.addHeader(headers, "Accept", "application/xml");
    extension.addHeader(headers, "X-Trace-Id", "abc-123");

    assertEquals("ok", extension.get(url, headers));
    assertEquals("application/xml", lastAcceptHeader.get());
    assertEquals("abc-123", lastTraceHeader.get());
  }

  private HttpServer startServer(int statusCode, String responseBody) throws IOException {
    HttpServer localServer = HttpServer.create(new InetSocketAddress(0), 0);
    localServer.createContext("/test", exchange -> handleRequest(exchange, statusCode, responseBody));
    localServer.setExecutor(null);
    localServer.start();
    return localServer;
  }

  private void handleRequest(HttpExchange exchange, int statusCode, String responseBody) throws IOException {
    lastAcceptHeader.set(getHeaderValue(exchange, "Accept"));
    lastTraceHeader.set(getHeaderValue(exchange, "X-Trace-Id"));

    byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
    exchange.sendResponseHeaders(statusCode, responseBytes.length);

    try (exchange; OutputStream os = exchange.getResponseBody()) {
      os.write(responseBytes);
    }
  }

  private String getHeaderValue(HttpExchange exchange, String headerName) {
    for (Map.Entry<String, java.util.List<String>> entry : exchange.getRequestHeaders().entrySet()) {
      if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(headerName)) {
        return entry.getValue().isEmpty() ? null : entry.getValue().get(0);
      }
    }
    return null;
  }
}



