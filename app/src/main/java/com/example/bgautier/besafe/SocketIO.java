package com.example.bgautier.besafe;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketIO {
    private Socket socket;
    private final String TAG = "SocketIO";

    public SocketIO(String api, String userId, String token, Emitter.Listener onAlert) throws URISyntaxException, JSONException {
        this.socket = IO.socket(api);
        this.listen(onAlert);
        this.socket.connect();
        this.authenticate(token, userId);
    }

    public void disconnect() {
        this.socket.disconnect();
        this.socket = null;
    }

    private void authenticate(String token, String userId) throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", token);
        obj.put("userId", userId);
        this.socket.emit("authentication", obj);
    }

    private void listen(Emitter.Listener onAlert) {
        this.socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
               Log.d(TAG, "SocketIO connected");
            }
        });

        this.socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "SocketIO disconnected : " + args[0]);
            }
        });

        this.socket.on(Socket.EVENT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG,"SocketIO error");
            }
        });

        this.socket.on("alert", onAlert);
    }
}
