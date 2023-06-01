package com.bupt.charger.service;

import com.bupt.charger.util.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class WsService {

    @Autowired
    public WebSocketServer webSocketServer;

    @Async
    public void sendToUser(String username, String message) {
       webSocketServer.sendToUser(username, message);
    }

    @Async
    public void sendInfo(String message) {
        webSocketServer.sendInfo(message);
    }
}
