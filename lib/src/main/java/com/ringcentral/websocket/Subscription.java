package com.ringcentral.websocket;

import com.ringcentral.RestClient;
import com.ringcentral.definitions.SubscriptionInfo;

public class Subscription {
    private SubscriptionInfo _subscription;

    public Subscription(RestClient restClient, String[] eventFilters, EventListener eventListener) {

    }

    public void subscribe() throws com.ringcentral.RestException, java.io.IOException {

    }


    public SubscriptionInfo getSubscription() {
        return _subscription;
    }
}
