package com.ringcentral.websocket;

import com.ringcentral.RestClient;
import com.ringcentral.Utils;
import com.ringcentral.definitions.SubscriptionInfo;
import okhttp3.ResponseBody;

public class Subscription {
    private SubscriptionInfo _subscription;
    private RestClient _restClient;
    private String[] _eventFilters;
    private EventListener _eventListener;


    public Subscription(RestClient restClient, String[] eventFilters, EventListener eventListener) {
        this._restClient = restClient;
        this._eventFilters = eventFilters;
        this._eventListener = eventListener;
    }

    public void subscribe() throws com.ringcentral.RestException, java.io.IOException {
        ResponseBody responseBody = this._restClient.post("/restapi/oauth/wstoken");
        WsToken wsToken = Utils.gson.fromJson(responseBody.string(), WsToken.class);
        System.out.println(wsToken.ws_access_token);
    }

    public SubscriptionInfo getSubscription() {
        return _subscription;
    }
}
