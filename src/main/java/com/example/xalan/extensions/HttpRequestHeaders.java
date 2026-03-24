package com.example.xalan.extensions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeaders {

  private final Map<String, String> values = new HashMap<>();

  public HttpRequestHeaders put(String name, String value) {
    if (name != null && !name.isBlank() && value != null) {
      values.put(name, value);
    }
    return this;
  }

  public Map<String, String> asMap() {
    return Collections.unmodifiableMap(values);
  }
}

