package com.ringcentral.websocket;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ringcentral.Utils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MyWebSocketClient extends WebSocketClient {

    public CloseListener closeListener;
    public boolean debugMode = false;
    private final String[] _eventFilters;
    private final EventListener _eventListener;
    private final Timer timer;

    public MyWebSocketClient(URI serverUri, String[] eventFilters, EventListener eventListener) {
        super(serverUri);
        this._eventFilters = eventFilters;
        this._eventListener = eventListener;
        this.timer = new Timer();
        this.setConnectionLostTimeout(30);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        SubscriptionRequestBody requestBody = new SubscriptionRequestBody();
        requestBody.deliveryMode = new SubscriptionRequestBodyDeliveryMode();
        requestBody.deliveryMode.transportType = "WebSocket";
        requestBody.eventFilters = this._eventFilters;

        RequestHeaders requestHeaders = new RequestHeaders();
        requestHeaders.method = "POST";
        requestHeaders.path = "/restapi/v1.0/subscription";
        requestHeaders.type = "ClientRequest";
        requestHeaders.messageId = UUID.randomUUID().toString();

        Object[] array = new Object[2];
        array[0] = requestHeaders;
        array[1] = requestBody;

        this.send(Utils.gson.toJson(array));
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                RequestHeaders requestHeaders = new RequestHeaders();
                requestHeaders.type = "Heartbeat";
                requestHeaders.messageId = UUID.randomUUID().toString();
                Object[] array = new Object[1];
                array[0] = requestHeaders;
                MyWebSocketClient.this.send(Utils.gson.toJson(array));
            }
        }, 600000, 600000);  // send a heartbeat every 10 minutes
    }

    @Override
    public void onMessage(String message) {
        if (this.debugMode) {
            System.out.println("[debug mode] inbound message: " + message);
        }
        if (message.contains("\"type\":\"ServerNotification\"")) {
            JsonElement jsonElement = JsonParser.parseString(message);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            String secondObjectString = jsonArray.get(1).toString();
            this._eventListener.listen(secondObjectString);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (closeListener != null) {
            closeListener.listen(code, reason, remote);
        }
    }

    @Override
    public void onError(Exception ex) {
    }

    public void revoke() {
        this.timer.cancel();
        this.close();
    }

    @Override
    public void send(String text) {
        if (this.debugMode) {
            System.out.println("[debug-mode] outbound message: " + text);
        }
        super.send(text);
    }
}