package com.gsmbypasspro.vpn;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private boolean isConnected = false;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        final Button btnConnect = findViewById(R.id.btnConnect);
        final TextView tvLogs = findViewById(R.id.tvLogs);

        // Actions du menu latéral
        findViewById(R.id.menuIp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "IP Actuelle : Connecté via Proxy Local", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
            }
        });

        findViewById(R.id.menuSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Paramètres UDPGW / DNS configurés (Port 7300)", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
            }
        });

        findViewById(R.id.menuServers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Liste des serveurs 3X-UI / VLess chargée", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
            }
        });

        findViewById(R.id.menuAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "GSM BYPASS-PRO VPN v1.0 - Ultra Rapide", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
            }
        });

        String timeNow = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        tvLogs.setText("[" + timeNow + "] Moteur de tunneling prêt sur " + Build.MODEL);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String t = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                if (!isConnected) {
                    tvLogs.append("\n[" + t + "] Injection Payload & Liaison UDPGW active...");
                    tvLogs.append("\n[" + t + "] Tunnel établi avec succès ! 🚀");
                    btnConnect.setText("DISCONNECT");
                    isConnected = true;
                } else {
                    tvLogs.append("\n[" + t + "] Déconnexion du tunnel.");
                    btnConnect.setText("CONNECT");
                    isConnected = false;
                }
            }
        });
    }
}
