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

public class HttpXmlExtension {

  public String get(String url) {
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

  private String readBody(HttpEntity entity) throws IOException {
    if (entity == null) {
      return "";
    }
    return new String(entity.getContent().readAllBytes(), StandardCharsets.UTF_8);
  }
}


