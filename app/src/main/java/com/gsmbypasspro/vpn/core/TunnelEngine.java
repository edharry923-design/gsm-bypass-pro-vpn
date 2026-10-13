package com.gsmbypasspro.vpn.core;

import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TunnelEngine {
    private static final String TAG = "TunnelEngine";
    private boolean isRunning = false;

    // Analyse et application de la payload SSH / HTTP Custom
    String formatPayload(String rawPayload, String host, int port) {
        if (rawPayload == null) return "";
        return rawPayload
                .replace("[host]", host)
                .replace("[port]", String.valueOf(port))
                .replace("[host_port]", host + ":" + port)
                .replace("[crlf]", "\r\n")
                .replace("[method]", "CONNECT");
    }

    public void startConnection(String serverAddress, String payload, TunnelCallback callback) {
        isRunning = true;
        new Thread(() -> {
            try {
                callback.onLog("[Engine] Résolution de l'adresse : " + serverAddress);
                String[] parts = serverAddress.split(":");
                String host = parts[0];
                int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 443;

                callback.onLog("[Engine] Connexion au socket distant " + host + ":" + port + "...");
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(host, port), 10000);
                callback.onLog("[Engine] Socket connecté avec succès !");

                if (payload != null && !payload.isEmpty()) {
                    String formatted = formatPayload(payload, host, port);
                    callback.onLog("[Engine] Injection de la Payload / En-têtes...");
                    OutputStream out = socket.getOutputStream();
                    out.write(formatted.getBytes());
                    out.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String responseLine = reader.readLine();
                    callback.onLog("[Server Response] " + responseLine);
                }

                callback.onLog("[Tunnel] Connexion ultra-rapide établie 🚀");
                callback.onSuccess();

            } catch (Exception e) {
                Log.e(TAG, "Erreur Tunnel", e);
                callback.onError("[Erreur] " + e.getMessage());
            }
        }).start();
    }

    public void stopConnection() {
        isRunning = false;
    }

    public interface TunnelCallback {
        void onLog(String message);
        void onSuccess();
        void onError(String error);
    }
}
