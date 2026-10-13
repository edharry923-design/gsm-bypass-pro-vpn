package com.gsmbypasspro.vpn;

import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.gsmbypasspro.vpn.core.TunnelEngine;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int VPN_REQUEST_CODE = 1337;
    private TextView tvLogs;
    private Button btnConnect;
    private EditText etTargetServer, etPayload;
    private boolean isConnected = false;
    private TunnelEngine tunnelEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("GSM BYPASS-PRO");
        }

        tvLogs = findViewById(R.id.tvLogs);
        btnConnect = findViewById(R.id.btnConnect);
        etTargetServer = findViewById(R.id.etTargetServer);
        etPayload = findViewById(R.id.etPayload);
        tunnelEngine = new TunnelEngine();

        // Afficher les informations de l'appareil au démarrage (exactement comme DarkTunnel)
        String timeStr = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String deviceInfo = String.format("[%s] Running on %s %s (%s), Android %s (API %s). Version 1.0.0 Build 1.",
                timeStr, Build.MANUFACTURER, Build.MODEL, Build.DEVICE, Build.VERSION.RELEASE, Build.VERSION.SDK_INT);
        appendLog(deviceInfo);

        btnConnect.setOnClickListener(v -> toggleConnection());
    }

    private void toggleConnection() {
        if (!isConnected) {
            Intent intent = VpnService.prepare(this);
            if (intent != null) {
                startActivityForResult(intent, VPN_REQUEST_CODE);
            } else {
                onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
            }
        } else {
            stopVpnService();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK) {
            Intent serviceIntent = new Intent(this, GsmVpnService.class);
            startService(serviceIntent);

            String server = etTargetServer.getText().toString().trim();
            String payload = etPayload.getText().toString().trim();

            if (server.isEmpty()) {
                server = "localhost:22";
            }

            appendLog("[Engine] Initialisation de la connexion vers " + server + "...");
            
            tunnelEngine.startConnection(server, payload, new TunnelEngine.TunnelCallback() {
                @Override
                public void onLog(String message) {
                    runOnUiThread(() -> appendLog(message));
                }

                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        appendLog("[TUN] Interface réseau virtuelle active 🚀");
                        appendLog("[Succès] Tunnel établi avec succès !");
                        btnConnect.setText("DISCONNECT");
                        isConnected = true;
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        appendLog(error);
                        stopVpnService();
                    });
                }
            });

        } else if (requestCode == VPN_REQUEST_CODE) {
            Toast.makeText(this, "Permission VPN refusée.", Toast.LENGTH_SHORT).show();
            appendLog("[Erreur] Permission VPN refusée.");
        }
    }

    private void stopVpnService() {
        tunnelEngine.stopConnection();
        Intent serviceIntent = new Intent(this, GsmVpnService.class);
        serviceIntent.setAction("STOP");
        startService(serviceIntent);

        appendLog("[Tunnel] Déconnecté.");
        btnConnect.setText("CONNECT");
        isConnected = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_import) {
            Toast.makeText(this, "Importation de configuration .gsm", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_export) {
            startActivity(new Intent(this, ExportActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            new AlertDialog.Builder(this)
                    .setTitle("À propos")
                    .setMessage("GSM BYPASS-PRO VPN v1.0\nDéveloppé pour un tunneling haute performance.")
                    .setPositiveButton("OK", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void appendLog(String msg) {
        String timeStr = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        tvLogs.append("[" + timeStr + "] " + msg + "\n");
    }
}
