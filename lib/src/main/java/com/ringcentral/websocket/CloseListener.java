package com.ringcentral.websocket;

public interface CloseListener {
    void listen(int code, String reason, boolean remote);
}
