package com.example.xalan.extensions;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HttpXmlExtensionXsltTest {

  private HttpServer server;
  private String lastAcceptHeader;
  private String lastTraceHeader;

  @AfterEach
  void tearDown() {
    if (server != null) {
      server.stop(0);
    }
  }

  @Test
  void xsltCallsHttpXmlExtensionMethod() throws Exception {
    server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/payload", this::handleRequest);
    server.start();

    String url = "http://localhost:" + server.getAddress().getPort() + "/payload";

    try (InputStream xsltStream = getClass().getResourceAsStream("/xslt/http-extension.xsl")) {
      assertNotNull(xsltStream, "Could not find /xslt/http-extension.xsl on the test classpath");

      Source xslt = new StreamSource(xsltStream);
      Source input = new StreamSource(new StringReader("<root/>"));

      Transformer transformer = new TransformerFactoryImpl().newTransformer(xslt);
      transformer.setParameter("url", url);
      transformer.setParameter("accept", "application/xml");
      transformer.setParameter("traceId", "from-xslt");

      StringWriter output = new StringWriter();
      transformer.transform(input, new StreamResult(output));

      String bodyText = extractBodyText(output.toString());
      assertEquals("hello-from-http", bodyText);
      assertEquals("application/xml", lastAcceptHeader);
      assertEquals("from-xslt", lastTraceHeader);
    }
  }

  private void handleRequest(HttpExchange exchange) throws IOException {
    lastAcceptHeader = getHeaderValue(exchange, "Accept");
    lastTraceHeader = getHeaderValue(exchange, "X-Trace-Id");

    byte[] responseBytes = "hello-from-http".getBytes(StandardCharsets.UTF_8);
    exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
    exchange.sendResponseHeaders(200, responseBytes.length);

    try (exchange; OutputStream os = exchange.getResponseBody()) {
      os.write(responseBytes);
    }
  }

  private String getHeaderValue(HttpExchange exchange, String headerName) {
    for (Map.Entry<String, List<String>> entry : exchange.getRequestHeaders().entrySet()) {
      if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(headerName)) {
        return entry.getValue().isEmpty() ? null : entry.getValue().get(0);
      }
    }
    return null;
  }

  private String extractBodyText(String xml) throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(false);

    Document doc = dbf.newDocumentBuilder().parse(
        new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))
    );

    XPath xPath = XPathFactory.newInstance().newXPath();
    return xPath.evaluate("/result/body/text()", doc).trim();
  }
}


