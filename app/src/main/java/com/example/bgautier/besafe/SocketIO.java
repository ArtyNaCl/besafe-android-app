package com.example.bgautier.besafe;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIO {
    private Socket socket;

    public SocketIO(String api, String token, Emitter.Listener onAlert) throws URISyntaxException {
        this.socket = IO.socket(api);
        this.listen(onAlert);
        this.socket.connect();
        this.authenticate(token);
    }

    public void disconnect() {
        this.socket.disconnect();
        this.socket = null;
    }

    private void authenticate(String token) {
        this.socket.emit("authenticate", token);
    }

    private void listen(Emitter.Listener onAlert) {
        this.socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Socket.IO connected");
            }
        });

        this.socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Socket.IO disconnected");
            }
        });

        this.socket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Socket.IO error");
            }
        });

        this.socket.on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Socket.IO message : " + args[0]);
            }
        });


        this.socket.on("alert", onAlert);
    }
}
