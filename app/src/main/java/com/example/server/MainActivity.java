package com.example.server;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketListener;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ServerActivity";

    ServerSocket serverSocket;
    SocketChannel channel;
    Selector selector;
    Server server;
    Thread Thread1 = null;
    TextView tvIP, tvPort;
    TextView tvMessages;
    EditText etMessage;
    Button btnSend;
    public static String SERVER_IP = "";
    public static final int SERVER_PORT = 11000;
    public static final int DEFAULT_BUFFER_SIZE = 10240;
    String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvIP = findViewById(R.id.tvIP);
        tvPort = findViewById(R.id.tvPort);
        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        try {
            SERVER_IP = getLocalIpAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Thread1 = new Thread(new Thread1());
        Thread1.start();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = etMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    new Thread(new Thread3(message)).start();
                }
            }
        });
    }
    private String getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
    }
    private PrintWriter output;
    private BufferedReader input;
    class Thread1 implements Runnable {
        @Override
        public void run() {
            try {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMessages.setText("Not connected");
                        tvIP.setText("IP: " + SERVER_IP);
                        tvPort.setText("Port: " + String.valueOf(SERVER_PORT));
                    }
                });
//                selector = Selector.open();
//                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
//                serverSocket = serverSocketChannel.socket();
//                serverSocket.bind(new InetSocketAddress(SERVER_PORT));
//                serverSocketChannel.configureBlocking(false);
//                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                server = new Server(SERVER_PORT, new WebSocketListener() {
                    @Override
                    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
                        Log.d(TAG, "received as a server");
                        return null;
                    }

                    @Override
                    public void onWebsocketHandshakeReceivedAsClient(WebSocket conn, ClientHandshake request, ServerHandshake response) throws InvalidDataException {
                        Log.d(TAG, "received as a client");
                    }

                    @Override
                    public void onWebsocketHandshakeSentAsClient(WebSocket conn, ClientHandshake request) throws InvalidDataException {
                        Log.d(TAG, "send as a client");
                    }

                    @Override
                    public void onWebsocketMessage(WebSocket conn, String message) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvMessages.append("Client: " + message + "\n");
                            }
                        });
                    }

                    @Override
                    public void onWebsocketMessage(WebSocket conn, ByteBuffer blob) {

                    }

                    @Override
                    public void onWebsocketMessageFragment(WebSocket conn, Framedata frame) {

                    }

                    @Override
                    public void onWebsocketOpen(WebSocket conn, Handshakedata d) {
                        Log.d(TAG, "onWebsocketOpen");
                    }

                    @Override
                    public void onWebsocketClose(WebSocket ws, int code, String reason, boolean remote) {
                        Log.d(TAG, "onWebsocketClose");
                    }

                    @Override
                    public void onWebsocketClosing(WebSocket ws, int code, String reason, boolean remote) {
                        Log.d(TAG, "onWebsocketClosing");
                    }

                    @Override
                    public void onWebsocketCloseInitiated(WebSocket ws, int code, String reason) {
                        Log.d(TAG, "onWebsocketCloseInitiated");
                    }

                    @Override
                    public void onWebsocketError(WebSocket conn, Exception ex) {
                        Log.d(TAG, "onWebsocketError");

                    }

                    @Override
                    public void onWebsocketPing(WebSocket conn, Framedata f) {
                        Log.d(TAG, "onWebsocketPing");

                    }

                    @Override
                    public void onWebsocketPong(WebSocket conn, Framedata f) {
                        Log.d(TAG, "onWebsocketPong");

                    }

                    @Override
                    public void onWriteDemand(WebSocket conn) {
                        Log.d(TAG, "onWriteDemand");

                    }

                    @Override
                    public InetSocketAddress getLocalSocketAddress(WebSocket conn) {
                        return null;
                    }

                    @Override
                    public InetSocketAddress getRemoteSocketAddress(WebSocket conn) {
                        return null;
                    }
                });
                server.start();
                Log.d(TAG, "Server started on port: " + server.getPort());

                runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvMessages.setText("Connected\n");
                                    Log.d(TAG, "Connected");
                                }
                            });

            } catch (IOException e) {
                Log.e(TAG, "Connect Error1: " + e.getMessage());
            }

//            while (true){
//                try {
//                    Log.d(TAG, "Waiting for client connection request");
//                    selector.select();
//                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
//
//                    while(keys.hasNext()){
//                        SelectionKey key = keys.next();
//                        keys.remove();
//
//                        if (key.isAcceptable()){
//                            Log.d(TAG, "Key is accepted");
//                            ServerSocketChannel server = (ServerSocketChannel) key.channel();
//                            channel = server.accept();
//                            channel.configureBlocking(false);
//                            channel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    tvMessages.setText("Connected\n");
//                                    Log.d(TAG, "Connected");
//                                }
//                            });
//                        }
//                        else if (key.isReadable()){
//                            Log.d(TAG, "Key is read");
//                            SocketChannel channel = (SocketChannel) key.channel();
//                            ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE);                            channel.read(buffer);
//                            String s = new String(buffer.array());
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    tvMessages.append("client:" + s + "\n");
//                                }
//                            });
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }
    class Thread3 implements Runnable {
        private String message;
        Thread3(String message) {
            this.message = message;
        }
        @Override
        public void run() {
//            if (channel != null && selector != null) {
//                try {
//                    ByteBuffer bytes = ByteBuffer.wrap(message.getBytes());
//                    Log.d(TAG, "ClientConnection: send bytes: " + bytes.remaining());
//                    int reqSize = bytes.remaining();
//                    int wroteSize = 0;
//                    int remainSize = bytes.remaining();
//                    while (remainSize > 0) {
//                        int len = channel.write(bytes);
//                        if (len < 0) {
//                            Log.d(TAG, "ClientConnection: write failure close connection");
//                            break;
//                        }
//                        wroteSize += len;
//                        remainSize = bytes.remaining();
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            tvMessages.append("Server: " + message + "\n");
//                            etMessage.setText("");
//                        }
//                    });
//
//                    if (reqSize == wroteSize) {
//                    } else {
//                        Log.d(TAG, "ClientConnection: not wrote all bytes reqSize:" + reqSize + " wroteSize:" + wroteSize);
//                    }
//                } catch (IOException e) {
//                    Log.e(TAG, "ClientConnection: close connection : exception " + e.getClass() + " : " + e.getMessage());
//                }
//            }
            if (server != null){
                server.broadcast(message);
                Log.d(TAG, message);
                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvMessages.append("Server: " + message + "\n");
                            etMessage.setText("");
                        }
                    });
            }
        }
    }
}