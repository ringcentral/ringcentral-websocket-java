# RingCentral WebSocket SDK for Java

## Installation

### Gradle

```
repositories {
  mavenCentral()
}

dependencies {
  implementation 'com.ringcentral:ringcentral-websocket:[version]'
}
```

### Maven

```xml
<dependency>
  <groupId>com.ringcentral</groupId>
  <artifactId>ringcentral-websocket</artifactId>
  <version>[version]</version>
</dependency>
```

Don't forget to replace the `[version]` with the expected version. You can find the latest version
in [Maven Central](https://central.sonatype.com/search?q=ringcentral-websocket).


### Sample usage

```java
Subscription subscription = new Subscription(rc,
    new String[]{"/restapi/v1.0/account/~/extension/~/message-store"},
    (jsonString) -> {
        // do something with the json string
    }
);

subscription.subscribe();
```
