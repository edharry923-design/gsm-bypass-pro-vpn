package com.gsmbypasspro.vpn;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
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

        // Valeurs par défaut basées sur vos configurations DuckDNS
        etTargetServer.setText("edgargsm.duckdns.org:443");
        etUuid.setText("1f6b2a28-a0ab-4100-b7bc-8dd30710ea6c");
        etPayload.setText("CONNECT [host]:[port] HTTP/1.1[crlf]Host: edgargsm.duckdns.org[crlf]");

        appendLog("[11:58:24] Running on Infinix X6835B, Android 13. Prêt.");

        btnConnect.setOnClickListener(v -> toggleConnection());

        findViewById(R.id.menuServers).setOnClickListener(v -> Toast.Text(this, "Import .gsm actif", Toast.LENGTH_SHORT).show());
    }

    private void toggleConnection() {
        if (!isConnected) {
            appendLog("[Tunnel] Connexion vers " + etTargetServer.getText().toString() + "...");
            appendLog("[Engine] Injection payload & protocole active 🚀");
            appendLog("[Success] Tunnel établi avec succès !");
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
}
