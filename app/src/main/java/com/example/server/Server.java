package com.example.server;

import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketListener;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class Server extends WebSocketServer {
    private static final String TAG = "Server";
    private WebSocketListener webSocketListener;
    public Server(int port, WebSocketListener webSocketListener) throws UnknownHostException {
        super(new InetSocketAddress(port));
        this.webSocketListener = webSocketListener;
    }

    public Server(InetSocketAddress address, WebSocketListener webSocketListener) {
        super(address);
        this.webSocketListener = webSocketListener;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.send("Welcome to the server!"); //This method sends a message to the new client
        Log.d(TAG, conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Log.d(TAG,conn + " disconnected!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Log.d(TAG,conn + ": " + message);
        webSocketListener.onWebsocketMessage(conn, message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Log.e(TAG,conn + "Error : " + ex.getMessage());
    }

    @Override
    public void onStart() {
        Log.d(TAG, "Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }
}
