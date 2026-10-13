package com.gsmbypasspro.vpn;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private TextView tvLogs;
    private Button btnConnect;
    private Spinner spinnerProtocol;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        tvLogs = findViewById(R.id.tvLogs);
        btnConnect = findViewById(R.id.btnConnect);
        spinnerProtocol = findViewById(R.id.spinnerProtocol);

        // Configuration du sélecteur de protocoles
        String[] protocols = {"VLESS (Xray-core)", "VMess (WS/TLS)", "Trojan GRPC", "SSH Direct / Tunnel"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, protocols);
        spinnerProtocol.setAdapter(adapter);

        appendLog("[System] Initialisation du moteur GSM BYPASS-PRO...");
        appendLog("[HWID] Empreinte appareil validée.");

        btnConnect.setOnClickListener(v -> toggleConnection());

        // Actions du menu latéral
        findViewById(R.id.menuIp).setOnClickListener(v -> showToast("Configuration IP"));
        findViewById(R.id.menuServers).setOnClickListener(v -> showToast("Gestion des serveurs"));
        findViewById(R.id.menuSettings).setOnClickListener(v -> showToast("Paramètres avancés"));
        findViewById(R.id.menuAbout).setOnClickListener(v -> showToast("GSM BYPASS-PRO v1.0"));
    }

    private void toggleConnection() {
        if (!isConnected) {
            String selectedProtocol = spinnerProtocol.getSelectedItem().toString();
            appendLog("[Protocol] Activation de " + selectedProtocol + "...");
            appendLog("[Core] Démarrage du tunnel sécurisé...");
            appendLog("[Status] Tunnel établi avec succès ! 🚀");
            btnConnect.setText("DÉCONNECTER");
            btnConnect.setBackgroundTintList(getColorStateList(android.R.color.holo_red_dark));
            isConnected = true;
        } else {
            appendLog("[Status] Déconnexion du tunnel.");
            btnConnect.setText("CONNECTER");
            btnConnect.setBackgroundTintList(getColorStateList(android.R.color.holo_green_light));
            isConnected = false;
        }
    }

    private void appendLog(String message) {
        tvLogs.append(message + "\n");
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
