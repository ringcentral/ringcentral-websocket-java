# RingCentral WebSocket SDK for Java

# RingCentral PubNub SDK for Java

This project is an extension of the [RingCentral SDK for Java](https://github.com/ringcentral/ringcentral-java) project.

## Getting help and support

If you are having difficulty using this SDK, or working with the RingCentral API, please visit our [developer community forums](https://community.ringcentral.com/spaces/144/) for help and to get quick answers to your questions. If you wish to contact the RingCentral Developer Support team directly, please [submit a help ticket](https://developers.ringcentral.com/support/create-case) from our developer website.

## Installation

### Gradle

```groovy
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
in [Maven Central](https://mvnrepository.com/artifact/com.ringcentral/ringcentral-websocket).


### Sample usage

```java
RestClient rc = new RestClient(clientId, clientSecret, server);
rc.authorize(jwtToken);

Subscription subscription = new Subscription(rc,
    new String[]{"/restapi/v1.0/account/~/extension/~/message-store"},
    (jsonString) -> {
        // do something with the json string
    }
);

subscription.subscribe();
```


## For maintainers


### Test

```
./gradlew test
```

### Release

Update the version number in `build.gradle`.

```
./gradlew publish
```

Go to https://s01.oss.sonatype.org/#stagingRepositories

Login, "Close" and "Release" the SDK.
