package com.gsmbypasspro.vpn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.VpnService;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.gsmbypasspro.vpn.core.TunnelEngine;

public class MainActivity extends AppCompatActivity {
    private static final int VPN_REQUEST_CODE = 1337;
    private static final int PICK_CONFIG_FILE = 100;
    private TextView tvLogs;
    private Button btnConnect;
    private Spinner spinnerProtocol;
    private EditText etTargetServer, etUuid, etPayload;
    private boolean isConnected = false;
    private TunnelEngine tunnelEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int themeMode = prefs.getInt("theme_mode", 0);
        if (themeMode == 0) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        else if (themeMode == 1) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        setContentView(R.layout.activity_main);

        tvLogs = findViewById(R.id.tvLogs);
        btnConnect = findViewById(R.id.btnConnect);
        spinnerProtocol = findViewById(R.id.spinnerProtocol);
        etTargetServer = findViewById(R.id.etTargetServer);
        etUuid = findViewById(R.id.etUuid);
        etPayload = findViewById(R.id.etPayload);
        tunnelEngine = new TunnelEngine();

        String[] protocols = {"Shell sécurisé (SSH)", "V2Ray / Xray", "Hysteria", "DNSTT (DNS)", "Shadowsocks"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, protocols);
        spinnerProtocol.setAdapter(adapter);

        spinnerProtocol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = protocols[position];
                appendLog("[Protocole] Mode : " + selected);
                if (selected.contains("SSH")) {
                    etTargetServer.setText("localhost:22");
                    etUuid.setText("root:password");
                    etPayload.setText("CONNECT [host_port] HTTP/1.1[crlf]Host: [host][crlf][crlf]");
                } else {
                    etTargetServer.setText("edgargsm.duckdns.org:443");
                    etUuid.setText("1f6b2a28-a0ab-4100-b7bc-8dd30710ea6c");
                    etPayload.setText("Websocket SNI / Path chiffré");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        appendLog("[Système] GSM BYPASS-PRO VPN prêt à l'emploi.");
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
            // Lancer le service TUN virtuel
            Intent serviceIntent = new Intent(this, GsmVpnService.class);
            startService(serviceIntent);

            // Démarrer le moteur de tunneling et l'injection de payload
            String server = etTargetServer.getText().toString();
            String payload = etPayload.getText().toString();

            tunnelEngine.startConnection(server, payload, new TunnelEngine.TunnelCallback() {
                @Override
                public void onLog(String message) {
                    runOnUiThread(() -> appendLog(message));
                }

                @Override
                public void onSuccess() {
                    runOnUiThread(() -> {
                        appendLog("[TUN] Interface réseau virtuelle active 🚀");
                        appendLog("[Succès] Tunnel haute vitesse établi !");
                        btnConnect.setText("Arrêter");
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

        } else if (requestCode == PICK_CONFIG_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                appendLog("[Config] Fichier chargé : " + uri.getLastPathSegment());
                Toast.makeText(this, "Configuration importée avec succès !", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == VPN_REQUEST_CODE) {
            Toast.makeText(this, "Permission VPN refusée.", Toast.LENGTH_SHORT).show();
            appendLog("[Erreur] Permission VPN refusée par l'utilisateur.");
        }
    }

    private void stopVpnService() {
        tunnelEngine.stopConnection();
        Intent serviceIntent = new Intent(this, GsmVpnService.class);
        serviceIntent.setAction("STOP");
        startService(serviceIntent);

        appendLog("[Tunnel] Connexion fermée.");
        btnConnect.setText("Démarrer");
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
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_CONFIG_FILE);
            return true;
        } else if (id == R.id.action_export) {
            startActivity(new Intent(this, ExportActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            new AlertDialog.Builder(this)
                    .setTitle("À propos")
                    .setMessage("GSM BYPASS-PRO VPN v1.0\n\nMoteur de tunneling professionnel haute vitesse.\nCopyright (c) 2026.")
                    .setPositiveButton("OK", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void appendLog(String msg) {
        tvLogs.append(msg + "\n");
    }
}
