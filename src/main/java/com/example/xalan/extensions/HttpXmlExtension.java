package com.example.xalan.extensions;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.util.Timeout;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpXmlExtension {

  public String get(String url) {
    return get(url, null);
  }

  public String get(String url, HttpRequestHeaders headers) {
    return executeGet(url, request -> applyHeaders(request, headers));
  }

  public HttpRequestHeaders newHeaders() {
    return new HttpRequestHeaders();
  }

  public HttpRequestHeaders addHeader(HttpRequestHeaders headers, String name, String value) {
    HttpRequestHeaders target = headers == null ? new HttpRequestHeaders() : headers;
    target.put(name, value);
    return target;
  }

  private String executeGet(String url, RequestCustomizer customizer) {
    ConnectionConfig connectionConfig = ConnectionConfig.custom()
        .setConnectTimeout(Timeout.ofSeconds(5))
        .setSocketTimeout(Timeout.ofSeconds(10))
        .build();

    PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
        .setDefaultConnectionConfig(connectionConfig)
        .build();

    try (CloseableHttpClient httpClient = HttpClients.custom()
        .setConnectionManager(connectionManager)
        .build()) {

      HttpGet request = new HttpGet(url);
      customizer.apply(request);

      HttpClientResponseHandler<String> responseHandler = response -> {
        int statusCode = response.getCode();
        String body = readBody(response.getEntity());

        if (statusCode >= 200 && statusCode < 300) {
          return body;
        }

        throw new RuntimeException("HTTP GET failed with status " + statusCode + " for URL: " + url);
      };

      return httpClient.execute(request, responseHandler);
    } catch (IOException | IllegalArgumentException ex) {
      throw new RuntimeException("HTTP GET request failed for URL: " + url, ex);
    }
  }

  private void applyHeaders(HttpGet request, HttpRequestHeaders headers) {
    if (headers == null) {
      return;
    }

    for (Map.Entry<String, String> entry : headers.asMap().entrySet()) {
      String name = entry.getKey();
      String value = entry.getValue();
      if (name != null && !name.isBlank() && value != null) {
        request.setHeader(name, value);
      }
    }
  }

  private String readBody(HttpEntity entity) throws IOException {
    if (entity == null) {
      return "";
    }
    return new String(entity.getContent().readAllBytes(), StandardCharsets.UTF_8);
  }

  @FunctionalInterface
  private interface RequestCustomizer {
    void apply(HttpGet request);
  }
}


