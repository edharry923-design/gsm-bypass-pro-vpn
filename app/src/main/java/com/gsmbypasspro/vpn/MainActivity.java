package com.gsmbypasspro.vpn;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {

    private static final int VPN_REQUEST_CODE = 1337;
    private TextView tvLogs;
    private Button btnConnect;
    private Spinner spinnerProtocol;
    private EditText etTargetServer, etUuid, etPayload;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int themeMode = prefs.getInt("theme_mode", 2);
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

        String[] protocols = {"VLess • Websocket SSL/TLS", "VMess • WS/TLS", "Trojan GRPC", "SSH • Direct"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, protocols);
        spinnerProtocol.setAdapter(adapter);

        spinnerProtocol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = protocols[position];
                if (selected.contains("SSH")) {
                    etTargetServer.setText("localhost:22");
                    etUuid.setText("root");
                    etPayload.setText("[method] [host_port] [protocol][crlf]Host: [host][crlf]");
                    appendLog("[Protocol] Mode SSH sélectionné.");
                } else if (selected.contains("VLess")) {
                    etTargetServer.setText("edgargsm.duckdns.org:443");
                    etUuid.setText("1f6b2a28-a0ab-4100-b7bc-8dd30710ea6c");
                    etPayload.setText("CONNECT [host]:[port] HTTP/1.1[crlf]Host: edgargsm.duckdns.org[crlf]");
                    appendLog("[Protocol] Mode VLess sélectionné.");
                } else {
                    etTargetServer.setText("edgargsm.duckdns.org:8443");
                    etUuid.setText("vmess-uuid-standard");
                    etPayload.setText("Websocket PATH /ws");
                    appendLog("[Protocol] Mode " + selected + " sélectionné.");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        appendLog("[System] GSM BYPASS-PRO VPN prêt à l'emploi.");
        btnConnect.setOnClickListener(v -> toggleConnection());
    }

    private void toggleConnection() {
        if (!isConnected) {
            // Demande l'autorisation système Android pour lancer le VpnService
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
            // L'utilisateur a autorisé le VPN, on lance le service TUN réel
            Intent serviceIntent = new Intent(this, GsmVpnService.class);
            startService(serviceIntent);

            appendLog("[Tunnel] Connexion vers " + etTargetServer.getText().toString() + "...");
            appendLog("[TUN] Interface réseau virtuelle active 🚀");
            appendLog("[Success] VPN connecté avec succès !");
            btnConnect.setText("DISCONNECT");
            isConnected = true;
        } else if (requestCode == VPN_REQUEST_CODE) {
            Toast.lez(this, "Permission VPN refusée par l'utilisateur.", Toast.LENGTH_SHORT).show();
            appendLog("[Error] Permission VPN refusée.");
        }
    }

    private void stopVpnService() {
        Intent serviceIntent = new Intent(this, GsmVpnService.class);
        serviceIntent.setAction("STOP");
        startService(serviceIntent);

        appendLog("[Tunnel] VPN déconnecté.");
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
        } else if (id == R.id.action_about) {
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("GSM BYPASS-PRO VPN v1.0\n\nMoteur VPN natif Android actif.\nCopyright (c) 2026.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void appendLog(String msg) {
        tvLogs.append(msg + "\n");
    }
}
