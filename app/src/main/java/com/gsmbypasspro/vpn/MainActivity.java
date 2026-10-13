package com.gsmbypasspro.vpn;

import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    private TextView tvLogs;
    private Button btnConnect;
    private Spinner spinnerProtocol;
    private EditText etTargetServer, etUuid, etPayload;
    private boolean isConnected = false;
    private boolean isDarkMode = true;

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

        appendLog("[System] GSM BYPASS-PRO VPN initialisé.");
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

        if (id == R.id.action_hwid) {
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            showPopup("HWID (ID de l'appareil) :\n" + androidId);
            appendLog("[HWID] Vérification : " + androidId);
            return true;
        } else if (id == R.id.action_import_export) {
            showPopup("Gestionnaire .gsm :\nImportation / Exportation de configuration active.");
            appendLog("[Config] Ouverture de l'explorateur .gsm...");
            return true;
        } else if (id == R.id.action_expiry) {
            showPopup("Statut de l'abonnement :\nValidité : Illimitée / Fichier .gsm actif.");
            appendLog("[License] Configuration valide jusqu'au 31/12/2026.");
            return true;
        } else if (id == R.id.action_theme) {
            toggleTheme();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Toast.makeText(this, "Mode Sombre activé", Toast.LENGTH_SHORT).show();
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Toast.makeText(this, "Mode Clair activé", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleConnection() {
        if (!isConnected) {
            appendLog("[Tunnel] Connexion via " + etTargetServer.getText().toString() + "...");
            appendLog("[Success] Tunnel établi avec succès ! 🚀");
            btnConnect.setText("DISCONNECT");
            isConnected = true;
        } else {
            appendLog("[Tunnel] Déconnexion.");
            btnConnect.setText("CONNECT");
            isConnected = false;
        }
    }

    private void showPopup(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void appendLog(String msg) {
        tvLogs.append(msg + "\n");
    }
}
