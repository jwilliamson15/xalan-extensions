# xalan-extensions

Starter Gradle Java project configured so `build` produces a jar artifact.

## Build

```bash
./gradlew clean build
```

Jar output:

- `build/libs/xalan-extensions-0.1.0.jar`

## Minimal Xalan Extension

- Class: `com.example.xalan.extensions.HelloXmlExtension`
- Method: `greet(String name)`
- Example return value: `greet("Josh")` -> `Hello Josh`

## HTTP GET Extension

- Class: `com.example.xalan.extensions.HttpXmlExtension`
- Method: `get(String url)`
- Method: `get(String url, HttpRequestHeaders headers)`
- Helper methods: `newHeaders()` and `addHeader(HttpRequestHeaders, String, String)`
- Uses: Apache HttpClient 5
- Behavior: returns response body for `2xx` responses, throws `RuntimeException` for non-`2xx` or transport errors

## Xalan XSLT Example

- XSLT file: `src/test/resources/xslt/hello-extension.xsl`
- Extension namespace: `xalan://com.example.xalan.extensions.HelloXmlExtension`
- Example call: `ext:greet($extObj, 'Josh')`

## Xalan HTTP XSLT Example

- XSLT file: `src/test/resources/xslt/http-extension.xsl`
- Extension namespace: `xalan://com.example.xalan.extensions.HttpXmlExtension`
- Example calls: `http:newHeaders($extObj)`, `http:addHeader($extObj, $headers, 'Accept', 'application/xml')`, `http:get($extObj, $url, $headers)`

## Tests

- Unit test: `src/test/java/com/example/xalan/extensions/HelloXmlExtensionTest.java`
- XSLT integration test: `src/test/java/com/example/xalan/extensions/HelloXmlExtensionXsltTest.java`
- Unit test: `src/test/java/com/example/xalan/extensions/HttpXmlExtensionTest.java`
- XSLT integration test: `src/test/java/com/example/xalan/extensions/HttpXmlExtensionXsltTest.java`

Run tests:

```bash
./gradlew test
```
