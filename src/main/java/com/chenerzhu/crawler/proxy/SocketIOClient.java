package com.chenerzhu.crawler.proxy;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;

public class SocketIOClient {

    private Socket socket;

    public SocketIOClient(String uri) throws URISyntaxException {
        // Connect to the Socket.IO server
        socket = IO.socket(uri);
        
        // Listen to the Sale Feed
        socket.on("saleFeed", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Sale Feed: " + args[0]);
            }
        });

        socket.on("*", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Sale Fee1d: " + args[0]);
            }
        });

        // Connect to the server
        socket.connect();

        // Join Sale Feed with parameters
        joinSaleFeed("EUR", "en", 730);
    }

    private void joinSaleFeed(String currency, String locale, int appId) {
        // Create the parameters object
        // In Java, you can use a Map or a custom object to mimic the parameters
        socket.emit("saleFeedJoin", new SaleFeedParams(currency, locale, appId));
        System.out.println("12312");
    }

    public static void main(String[] args) {
        try {
            new SocketIOClient("https://skinport.com");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    
    // Custom class to represent the parameters
    static class SaleFeedParams {
        String currency;
        String locale;
        int appId;

        public SaleFeedParams(String currency, String locale, int appId) {
            this.currency = currency;
            this.locale = locale;
            this.appId = appId;
        }
    }
}