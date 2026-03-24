package com.example.xalan.extensions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloXmlExtensionTest {

  @Test
  void greetReturnsExpectedMessage() {
    HelloXmlExtension extension = new HelloXmlExtension();
    assertEquals("Hello Josh", extension.greet("Josh"));
  }
}

