# RingCentral WebSocket SDK for Java

This project is an extension of the [RingCentral SDK for Java](https://github.com/ringcentral/ringcentral-java) project.

You will need to install the RingCentral SDK for Java first.

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


## Sample usage

```java
import com.ringcentral.RestClient;
import com.ringcentral.websocket.Subscription;

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


## How to keep it running 24 * 7?

The subscription shares the OAuth session with the `RestClient rc` object. If the OAuth session expires, the subscription will be automatically stopped. So you need to maintain a long-lived OAuth session to keep the subscription running 24 * 7.
Before your OAuth session expires, you need to `rc.refresh()` to refresh the OAuth session. You can do it in a timer task every 30 minutes. The OAuth session expires in 1 hour, it is OK to refresh it every 30 minutes, there is no need to refresh it in a shorter interval.

If for some reason your OAuth session got expired (access token expired but refresh token still valid), you need to `rc.refresh()` and then re-subscribe the subscription: `subscription.subscribe()`. It is because, as soon as your access token expired, the underlying WebSocket connection will be closed. 

If for some reason your OAuth session got revoked/invalidated, or you just want to use a new OAuth session. you need to re-authorize the `rc.authorize(...)` and then re-subscribe the subscription: `subscription.subscribe()`. It is because, as soon as your access token got revoked/invalidated, the underlying WebSocket connection will be closed.

There are other cases that the underlying WebSocket connection will be closed. One example: there is an `absoluteTimeout` value enforced by RingCentral server side. It means that the WebSocket connection will be closed after this timeout. The default value is 24 hours. Another example: there is network outage, and WebSocket connection closed due to network outage. In these cases, you need to re-subscribe the subscription: `subscription.subscribe()`.




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
