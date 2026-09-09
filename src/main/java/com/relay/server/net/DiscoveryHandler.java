package com.relay.server.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.relay.protocol.Message;
import com.relay.protocol.MessageAdapter;
import com.relay.protocol.MessageType;

public class DiscoveryHandler implements Runnable {
    private static final int DISCOVERY_PORT = 5000;
    private static final int CHAT_PORT = 5000;
    private static final Gson gson = new GsonBuilder()
                                        .registerTypeAdapter(Message.class, new MessageAdapter())
                                        .create();
    @Override 
    public void run() {
        try (DatagramSocket dSocket = new DatagramSocket(DISCOVERY_PORT)) {
            while (true) {
                byte[] receiveBuf = new byte[65535];
                DatagramPacket rcvdPacket = new DatagramPacket(receiveBuf, receiveBuf.length);
                
                dSocket.receive(rcvdPacket);
                String messageStr = new String(receiveBuf, 0, rcvdPacket.getLength(), StandardCharsets.UTF_8);
                Message message = gson.fromJson(messageStr, Message.class);
                
                if (message.getType() == MessageType.DISCOVERY_REQUEST) {
                    Message response = Message.builder(MessageType.ACK)
                        .body(String.valueOf(CHAT_PORT))
                        .build();
                    byte responseBuf[] = gson.toJson(response).getBytes();
                    dSocket.send(new DatagramPacket(responseBuf, responseBuf.length, rcvdPacket.getAddress(), rcvdPacket.getPort()));
                }
            }
        } catch (IOException e) {
            System.err.println("Error in UDP socket: " + e.getMessage());
        }
    }
}
