package com.ringcentral.websocket;

import com.ringcentral.RestClient;
import com.ringcentral.Utils;
import okhttp3.ResponseBody;

import java.net.URI;

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

class SubscriptionRequestBody {
    public SubscriptionRequestBodyDeliveryMode deliveryMode;
    public String[] eventFilters;
}

class SubscriptionRequestBodyDeliveryMode {
    public String transportType;
}

public class Subscription {
    public MyWebSocketClient webSocketClient;
    private final RestClient _restClient;
    private final String[] _eventFilters;
    private final EventListener _eventListener;

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
