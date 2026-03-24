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

## Xalan XSLT Example

- XSLT file: `src/test/resources/xslt/hello-extension.xsl`
- Extension namespace: `xalan://com.example.xalan.extensions.HelloXmlExtension`
- Example call: `ext:greet($extObj, 'Josh')`

## Tests

- Unit test: `src/test/java/com/example/xalan/extensions/HelloXmlExtensionTest.java`
- XSLT integration test: `src/test/java/com/example/xalan/extensions/HelloXmlExtensionXsltTest.java`

Run tests:

```bash
./gradlew test
```
