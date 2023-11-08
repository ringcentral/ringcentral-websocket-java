package com.ringcentral.websocket;

import com.ringcentral.RestClient;
import com.ringcentral.RestException;
import com.ringcentral.definitions.CreateInternalTextMessageRequest;
import com.ringcentral.definitions.PagerCallerInfoRequest;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class LibraryTest {
    @Test
    public void defaultTest() throws RestException, IOException, InterruptedException {
        RestClient rc = new RestClient(
                System.getenv("RINGCENTRAL_CLIENT_ID"),
                System.getenv("RINGCENTRAL_CLIENT_SECRET"),
                System.getenv("RINGCENTRAL_SERVER_URL")
        );

        rc.authorize(
                System.getenv("RINGCENTRAL_JWT_TOKEN")
        );
        final String[] message = {null};
        Subscription subscription = new Subscription(rc,
                new String[]{"/restapi/v1.0/account/~/extension/~/message-store"},
                (jsonString) -> {
                    message[0] = jsonString;
                }
        );

        subscription.subscribe();

        // send a company pager to trigger notifications
        rc.restapi().account().extension().companyPager().post(
                new CreateInternalTextMessageRequest().from(new PagerCallerInfoRequest().extensionId(rc.token.owner_id))
                        .to(new PagerCallerInfoRequest[]{new PagerCallerInfoRequest().extensionId(rc.token.owner_id)})
                        .text("Hello world")
        );

        Thread.sleep(20000);

        assertNotNull(message[0]);
        assertTrue(message[0].contains("uuid"));

        rc.revoke();
        System.out.println("quiting");
    }

    @Test
    public void closeTest() throws RestException, IOException, InterruptedException {
        RestClient rc = new RestClient(
                System.getenv("RINGCENTRAL_CLIENT_ID"),
                System.getenv("RINGCENTRAL_CLIENT_SECRET"),
                System.getenv("RINGCENTRAL_SERVER_URL")
        );

        rc.authorize(
                System.getenv("RINGCENTRAL_JWT_TOKEN")
        );
        Subscription subscription = new Subscription(rc,
                new String[]{"/restapi/v1.0/account/~/extension/~/message-store"},
                (jsonString) -> {
                }
        );

        subscription.subscribe();

        final boolean[] closed = {false};
        subscription.webSocketClient.closeListener = new CloseListener() {
            @Override
            public void listen(int code, String reason, boolean remote) {
                closed[0] = true;
            }
        };

        Thread.sleep(5000); // wait for connection stable

        assertFalse(closed[0]);
        subscription.revoke();
        System.out.println("quiting");
        Thread.sleep(3000);
        assertTrue(closed[0]);
        rc.revoke();
    }

    @Test
    public void debugModeTest() throws RestException, IOException, InterruptedException {
        RestClient rc = new RestClient(
                System.getenv("RINGCENTRAL_CLIENT_ID"),
                System.getenv("RINGCENTRAL_CLIENT_SECRET"),
                System.getenv("RINGCENTRAL_SERVER_URL")
        );

        rc.authorize(
                System.getenv("RINGCENTRAL_JWT_TOKEN")
        );
        Subscription subscription = new Subscription(rc,
                new String[]{"/restapi/v1.0/account/~/extension/~/message-store"},
                (jsonString) -> {
                }
        );

        subscription.subscribe();
        subscription.webSocketClient.debugMode = true;

        // send a company pager to trigger notifications
        rc.restapi().account().extension().companyPager().post(
                new CreateInternalTextMessageRequest().from(new PagerCallerInfoRequest().extensionId(rc.token.owner_id))
                        .to(new PagerCallerInfoRequest[]{new PagerCallerInfoRequest().extensionId(rc.token.owner_id)})
                        .text("Hello world")
        );

        Thread.sleep(20000);

        rc.revoke();
        System.out.println("quiting");
    }
}
