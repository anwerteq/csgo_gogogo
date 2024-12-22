package com.xiaojuzi;

import io.socket.client.IO;
import io.socket.client.Socket;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessageUnpacker;

import java.net.URISyntaxException;
import java.util.Arrays;

public class WebSocketClient {
    public static void main(String[] args) {
        IO.Options options = new IO.Options();
        options.transports = new String[]{"websocket"};
        options.reconnection = true;
        try {
            Socket socket = IO.socket("wss://skinport.com", options);
            socket.on(Socket.EVENT_CONNECT, args1 -> System.out.println("Connected to server"));
            socket.on(Socket.EVENT_CONNECT_ERROR, args1 -> System.err.println("Connection error: " + Arrays.toString(args1)));
            socket.on("error", args1 -> System.err.println("Error: " + Arrays.toString(args1)));

            socket.on("saleFeed", WebSocketClient::handleSaleFeed);

            socket.connect();

            socket.emit("saleFeedJoin", createMsgPackPayload("EUR", "en", 730));
            System.out.println("Socket.IO client connected and event emitted");
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid WebSocket URL", e);
        }
    }

    private static void handleSaleFeed(Object... args) {
        if (args.length > 0) {
            if (args[0] instanceof byte[]) {
                try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker((byte[]) args[0])) {
                    while (unpacker.hasNext()) {
                        System.out.println("Decoded Message: " + unpacker.unpackString());
                    }
                } catch (Exception e) {
                    System.err.println("Failed to unpack message: " + e.getMessage());
                }
            } else {
                System.out.println("Received non-binary data: " + args[0]);
            }
        } else {
            System.err.println("No data received from saleFeed.");
        }
    }

    private static byte[] createMsgPackPayload(String currency, String locale, int appid) {
        try (MessageBufferPacker packer = MessagePack.newDefaultBufferPacker()) {
            packer.packMapHeader(3);
            packer.packString("currency").packString(currency);
            packer.packString("locale").packString(locale);
            packer.packString("appid").packInt(appid);
            return packer.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error packing data", e);
        }
    }
}
