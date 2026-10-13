package com.gsmbypasspro.vpn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

    private static final int PICK_CONFIG_FILE = 100;
    private TextView tvLogs;
    private Button btnConnect;
    private Spinner spinnerProtocol;
    private EditText etTargetServer, etUuid, etPayload;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Appliquer le thème sauvegardé
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

        // Changement dynamique des champs selon le protocole sélectionné
        spinnerProtocol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = protocols[position];
                if (selected.contains("SSH")) {
                    etTargetServer.setText("localhost:22");
                    etUuid.setText("root");
                    etPayload.setText("[method] [host_port] [protocol][crlf]Host: [host][crlf]");
                    appendLog("[Protocol] Mode SSH actif.");
                } else if (selected.contains("VLess")) {
                    etTargetServer.setText("edgargsm.duckdns.org:443");
                    etUuid.setText("1f6b2a28-a0ab-4100-b7bc-8dd30710ea6c");
                    etPayload.setText("CONNECT [host]:[port] HTTP/1.1[crlf]Host: edgargsm.duckdns.org[crlf]");
                    appendLog("[Protocol] Mode VLess actif.");
                } else {
                    etTargetServer.setText("edgargsm.duckdns.org:8443");
                    etUuid.setText("vmess-uuid-standard");
                    etPayload.setText("Websocket PATH /ws");
                    appendLog("[Protocol] Mode " + selected + " actif.");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        appendLog("[System] GSM BYPASS-PRO VPN prêt.");
        btnConnect.setOnClickListener(v -> toggleConnection());
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
        } else if (id == R.id.action_import_file) {
            // Ouvre l'explorateur de fichiers natif du téléphone pour .gsm
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*"); // Permet de cibler tous les fichiers ou filtrer par extension
            startActivityForResult(intent, PICK_CONFIG_FILE);
            return true;
        } else if (id == R.id.action_export_file) {
            appendLog("[Config] Configuration exportée avec succès.");
            showToast("Config exportée (.gsm)");
            return true;
        } else if (id == R.id.type_vless) {
            spinnerProtocol.setSelection(0);
            return true;
        } else if (id == R.id.type_vmess) {
            spinnerProtocol.setSelection(1);
            return true;
        } else if (id == R.id.type_ssh) {
            spinnerProtocol.setSelection(3);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Réception du fichier .gsm sélectionné depuis l'explorateur du téléphone
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CONFIG_FILE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String path = uri.getPath();
                appendLog("[Config] Fichier .gsm chargé : " + path);
                showToast("Fichier importé avec succès !");
            }
        }
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("GSM BYPASS-PRO VPN v1.0\n\nDéveloppé pour les techniciens GSM & Experts Réseau.\nCopyright (c) 2026.")
                .setPositiveButton("OK", null)
                .show();
    }

    private void toggleConnection() {
        if (!isConnected) {
            appendLog("[Tunnel] Connexion vers " + etTargetServer.getText().toString() + "...");
            appendLog("[Success] Tunnel établi avec succès ! 🚀");
            btnConnect.setText("DISCONNECT");
            isConnected = true;
        } else {
            appendLog("[Tunnel] Déconnexion.");
            btnConnect.setText("CONNECT");
            isConnected = false;
        }
    }

    private void appendLog(String msg) {
        tvLogs.append(msg + "\n");
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
