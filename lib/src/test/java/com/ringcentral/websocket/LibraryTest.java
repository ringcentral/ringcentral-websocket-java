package com.ringcentral.websocket;

import com.ringcentral.RestClient;
import com.ringcentral.RestException;
import org.junit.Test;

import java.io.IOException;

public class LibraryTest {
    @Test
    public void defaultTest() throws RestException, IOException {
        RestClient rc = new RestClient(
                System.getenv("RINGCENTRAL_CLIENT_ID"),
                System.getenv("RINGCENTRAL_CLIENT_SECRET"),
                System.getenv("RINGCENTRAL_SERVER_URL")
        );

        rc.authorize(
                System.getenv("RINGCENTRAL_USERNAME"),
                System.getenv("RINGCENTRAL_EXTENSION"),
                System.getenv("RINGCENTRAL_PASSWORD")
        );
        Subscription subscription = new Subscription(rc,
                new String[]{"/restapi/v1.0/account/~/extension/~/message-store"},
                (jsonString) -> {
                }
        );
        subscription.subscribe();

        rc.revoke();
    }
}
