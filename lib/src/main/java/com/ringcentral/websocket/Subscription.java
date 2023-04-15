package com.ringcentral.websocket;

import com.ringcentral.RestClient;
import com.ringcentral.Utils;
import okhttp3.ResponseBody;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class Subscription {
    private RestClient _restClient;
    private String[] _eventFilters;
    private EventListener _eventListener;

    class MyWebSocketClient extends WebSocketClient {

        public MyWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            System.out.println("onOpen");
        }

        @Override
        public void onMessage(String message) {
            System.out.println("onMessage: " + message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            System.out.println("onClose: " + code + " " + reason);
        }

        @Override
        public void onError(Exception ex) {
            System.out.println("onError");
        }
    }


    public Subscription(RestClient restClient, String[] eventFilters, EventListener eventListener) {
        this._restClient = restClient;
        this._eventFilters = eventFilters;
        this._eventListener = eventListener;
    }

    public void subscribe() throws com.ringcentral.RestException, java.io.IOException {
        ResponseBody responseBody = this._restClient.post("/restapi/oauth/wstoken");
        WsToken wsToken = Utils.gson.fromJson(responseBody.string(), WsToken.class);
        String wsUri = wsToken.uri + "?access_token=" + wsToken.ws_access_token;
        MyWebSocketClient myClient = new MyWebSocketClient(URI.create(wsUri));
        myClient.connect();
    }
}
