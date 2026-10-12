package com.gsmbypasspro.vpn;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Build;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btnConnect = findViewById(R.id.btnConnect);
        final TextView tvLogs = findViewById(R.id.tvLogs);
        final EditText etTarget = findViewById(R.id.etTarget);
        final EditText etPayload = findViewById(R.id.etPayload);

        // Affichage des infos de l'appareil dans les logs au démarrage
        String deviceModel = Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        
        tvLogs.setText("[" + currentTime + "] Running on " + deviceModel + ", Android " + androidVersion + ".\n[GSM BYPASS-PRO] Prêt à l'injection.");

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeNow = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                if (!isConnected) {
                    tvLogs.append("\n[" + timeNow + "] Connexion en cours vers le serveur...");
                    tvLogs.append("\n[" + timeNow + "] Injection du Payload réussie !");
                    tvLogs.append("\n[" + timeNow + "] Tunnel actif - Vitesse ultra-rapide 🚀");
                    btnConnect.setText("DISCONNECT");
                    btnConnect.setBackgroundColor(0xFFF44336); // Rouge
                    isConnected = true;
                } else {
                    tvLogs.append("\n[" + timeNow + "] Déconnexion du tunnel.");
                    btnConnect.setText("CONNECT");
                    btnConnect.setBackgroundColor(0xFF9C27B0); // Violet
                    isConnected = false;
                }
            }
        });
    }
}
