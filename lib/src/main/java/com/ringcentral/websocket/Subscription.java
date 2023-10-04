package com.ringcentral.websocket;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ringcentral.RestClient;
import com.ringcentral.Utils;
import okhttp3.ResponseBody;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

class WsToken {
    public String uri;
    public String ws_access_token;
    public int expires_in;
}

class RequestHeaders {
    public String type;
    public String messageId;
    public String method;
    public String path;
}

class SubscriptionRequestBody
{
    public SubscriptionRequestBodyDeliveryMode deliveryMode;
    public String[] eventFilters;
}
class SubscriptionRequestBodyDeliveryMode
{
    public String transportType;
}

class MyWebSocketClient extends WebSocketClient {

    private String[] _eventFilters;
    private EventListener _eventListener;

    private Timer timer;

    public MyWebSocketClient(URI serverUri, String[] eventFilters, EventListener eventListener) {
        super(serverUri);
        this._eventFilters = eventFilters;
        this._eventListener = eventListener;
        this.timer = new Timer();
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
                }, 600000, 600000);  // run 10 minutes
    }

    @Override
    public void onMessage(String message) {
        if(message.contains("\"type\":\"ServerNotification\"")) {
            JsonElement jsonElement = JsonParser.parseString(message);
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            String secondObjectString = jsonArray.get(1).toString();
            this._eventListener.listen(secondObjectString);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {

    }

    @Override
    public void onError(Exception ex) {

    }

    public void revoke() {
        this.timer.cancel();
        this.close();
    }
}

public class Subscription {
    private RestClient _restClient;
    private String[] _eventFilters;
    private EventListener _eventListener;

    public MyWebSocketClient webSocketClient;

    public Subscription(RestClient restClient, String[] eventFilters, EventListener eventListener) {
        this._restClient = restClient;
        this._eventFilters = eventFilters;
        this._eventListener = eventListener;
    }

    public void subscribe() throws com.ringcentral.RestException, java.io.IOException {
        ResponseBody responseBody = this._restClient.post("/restapi/oauth/wstoken");
        WsToken wsToken = Utils.gson.fromJson(responseBody.string(), WsToken.class);
        String wsUri = wsToken.uri + "?access_token=" + wsToken.ws_access_token;
        webSocketClient = new MyWebSocketClient(URI.create(wsUri), _eventFilters, _eventListener);
        webSocketClient.connect();
    }

    public void revoke() {
        webSocketClient.revoke();
    }
}
