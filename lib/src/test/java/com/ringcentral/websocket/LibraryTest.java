package com.ringcentral.websocket;

import com.ringcentral.RestClient;
import com.ringcentral.RestException;
import com.ringcentral.definitions.CreateInternalTextMessageRequest;
import com.ringcentral.definitions.PagerCallerInfoRequest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LibraryTest {
    @Test
    public void defaultTest() throws RestException, IOException, InterruptedException {
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
        final String[] message = {null};
        Subscription subscription = new Subscription(rc,
                new String[]{"/restapi/v1.0/account/~/extension/~/message-store"},
                (jsonString) -> {
                   message[0] = jsonString;
                }
        );

        subscription.subscribe();

        rc.restapi().account().extension().companyPager().post(
                new CreateInternalTextMessageRequest().from(new PagerCallerInfoRequest().extensionNumber("101"))
                        .to(new PagerCallerInfoRequest[]{new PagerCallerInfoRequest().extensionNumber("101")})
                        .text("Hello world")
        );

        Thread.sleep(20000);

        assertNotNull(message[0]);
        assertTrue(message[0].contains("uuid"));

        rc.revoke();
        System.out.println("quiting");
    }
}
