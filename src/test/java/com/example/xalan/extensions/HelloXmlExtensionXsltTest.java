package com.example.xalan.extensions;

import org.apache.xalan.processor.TransformerFactoryImpl;
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
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HelloXmlExtensionXsltTest {

  @Test
  void xsltCallsHelloXmlExtensionMethod() throws Exception {
    try (InputStream xsltStream = getClass().getResourceAsStream("/xslt/hello-extension.xsl")) {
      assertNotNull(xsltStream, "Could not find /xslt/hello-extension.xsl on the test classpath");

      Source xslt = new StreamSource(xsltStream);
      Source input = new StreamSource(new StringReader("<root/>"));

      Transformer transformer = new TransformerFactoryImpl().newTransformer(xslt);
      StringWriter output = new StringWriter();
      transformer.transform(input, new StreamResult(output));

      String messageText = extractMessageText(output.toString());
      assertEquals("Hello Josh", messageText);
    }
  }

  private String extractMessageText(String xml) throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(false);

    Document doc = dbf.newDocumentBuilder().parse(
        new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))
    );

    XPath xPath = XPathFactory.newInstance().newXPath();
    return xPath.evaluate("/result/message/text()", doc).trim();
  }
}

