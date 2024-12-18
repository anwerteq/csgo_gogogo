package com.chenerzhu.crawler.proxy;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.net.URISyntaxException;

public class SocketIOClient {
    private static Socket socket;

    public static void main(String[] args) {
        try {
            // Configure Socket.IO client options
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket"}; // Use WebSocket transport
            options.reconnection = true; // Enable reconnection
            options.reconnectionAttempts = 5; // Max reconnection attempts
            options.reconnectionDelay = 2000; // Delay between attempts

            // Enable Socket.IO debug logs
            System.setProperty("io.socket.debug", "true");

            // Create and open Socket.IO connection
            socket = IO.socket("https://skinport.com", options);

            // Event listener for connection success
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Connected to server");
                    socket.emit("saleFeedJoin", "{\"currency\": \"EUR\", \"locale\": \"en\", \"appid\": 730}");
                }
            });

            // Listener for "saleFeed" event
            socket.on("saleFeed", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Received saleFeed: " + args[0]);
                }
            });

            // Event listener for connection errors
            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.err.println("Connection error: " + args[0]);
                }
            });

            socket.on("event_error", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.err.println("Error: " + args[0]);
                }
            });

            // Event listener for disconnection
            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Disconnected from server");
                }
            });

            // Connect to the server
            socket.connect();
            socket.emit("saleFeedJoin","{\"currency\": \"EUR\", \"locale\": \"en\", \"appid\": 730}");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
