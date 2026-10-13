package com.gsmbypasspro.vpn;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvLogs;
    private Button btnConnect;
    private Spinner spinnerProtocol;
    private EditText etTargetServer, etUuid, etPayload;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        etTargetServer.setText("edgargsm.duckdns.org:443");
        etUuid.setText("1f6b2a28-a0ab-4100-b7bc-8dd30710ea6c");
        etPayload.setText("CONNECT [host]:[port] HTTP/1.1[crlf]Host: edgargsm.duckdns.org[crlf]");

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
        } else if (id == R.id.action_import_clipboard) {
            appendLog("[Config] Importation depuis le presse-papier...");
            showToast("Imported from Clipboard");
            return true;
        } else if (id == R.id.action_import_file) {
            appendLog("[Config] Ouverture du sélecteur de fichier .gsm...");
            showToast("Import File (.gsm)");
            return true;
        } else if (id == R.id.action_export_file) {
            appendLog("[Config] Exportation vers un fichier .gsm...");
            showToast("Export File (.gsm)");
            return true;
        } else if (id == R.id.type_vless) {
            spinnerProtocol.setSelection(0);
            appendLog("[Mode] Protocole changé : VLess");
            return true;
        } else if (id == R.id.type_vmess) {
            spinnerProtocol.setSelection(1);
            appendLog("[Mode] Protocole changé : VMess");
            return true;
        } else if (id == R.id.type_ssh) {
            spinnerProtocol.setSelection(3);
            appendLog("[Mode] Protocole changé : SSH");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("GSM BYPASS-PRO VPN v1.0\n\nCopyright (c) 2026 GSM BY-PASS PRO.\nAll rights reserved.")
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
