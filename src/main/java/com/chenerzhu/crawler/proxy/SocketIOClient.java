package com.chenerzhu.crawler.proxy;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;

import java.net.URISyntaxException;

public class SocketIOClient {
    public static void main(String[] args) {
        try {
            // Configure Socket.IO client options
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket"}; // Use WebSocket transport
            options.reconnection = true;

            // Create and open Socket.IO connection
            Socket socket = IO.socket("https://socketio-chat-h9jt.herokuapp.com/", options);
            ObjectMapper msgPackMapper = new ObjectMapper(new MessagePackFactory());

            // Event listener for connection success
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Connected to server");

                    // Encode SaleFeedJoin object with MessagePack and send it
                    try {
                        byte[] encodedData = msgPackMapper.writeValueAsBytes(new SaleFeedJoin("EUR", "en", 730));
                        socket.emit("saleFeedJoin", "{\"currency\":\"EUR\",\"locale\":\"en\",\"appid\":730}");
                        System.out.println("Sent saleFeedJoin event");
                    } catch (Exception e) {
                        System.err.println("Error encoding saleFeedJoin: " + e.getMessage());
                    }
                }
            });

            // Listener for "saleFeed" event (MessagePack decoded)
            socket.on("saleFeed", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        if (args[0] instanceof byte[]) {
                            Object decodedData = msgPackMapper.readValue((byte[]) args[0], Object.class);
                            System.out.println("Received saleFeed: " + decodedData);
                        } else {
                            System.out.println("Received unexpected data: " + args[0]);
                        }
                    } catch (Exception e) {
                        System.err.println("Error decoding saleFeed: " + e.getMessage());
                    }
                }
            });

            // Event listener for connection errors
            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.err.println("Connection error: " + (args.length > 0 ? args[0] : "Unknown error"));
                }
            });

            // General error listener
            socket.on("error", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.err.println("General error: " + (args.length > 0 ? args[0] : "Unknown error"));
                }


            });

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Connected to server");
                    System.out.println("Emitting saleFeedJoin event...");
                    try {
                        byte[] encodedData = new ObjectMapper(new MessagePackFactory())
                                .writeValueAsBytes(new SaleFeedJoin("EUR", "en", 730));
                        socket.emit("saleFeedJoin", encodedData);
                    } catch (Exception e) {
                        System.err.println("Error emitting saleFeedJoin: " + e.getMessage());
                    }
                }
            });

            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.err.println("Connection error: " + (args.length > 0 ? args[0] : "Unknown"));
                }
            });

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Disconnected from server");
                }
            });

            socket.on("saleFeed", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("saleFeed event triggered");
                    if (args.length > 0) {
                        System.out.println("Received saleFeed data: " + args[0]);
                    } else {
                        System.out.println("No data received for saleFeed.");
                    }
                }
            });

            socket.on("*", new Emitter.Listener() { // Catch all events
                @Override
                public void call(Object... args) {
                    System.out.println("Received an unknown event: " + args[0]);
                }
            });


            // Connect to the server
            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Data model for SaleFeedJoin event
     */
    static class SaleFeedJoin {
        public String currency;
        public String locale;
        public int appid;

        public SaleFeedJoin(String currency, String locale, int appid) {
            this.currency = currency;
            this.locale = locale;
            this.appid = appid;
        }
    }
}
