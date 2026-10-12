package com.gsmbypasspro.vpn;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.Color;

public class MainActivity extends Activity {

    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tvStatus = findViewById(R.id.tvStatus);
        final Button btnConnect = findViewById(R.id.btnConnect);
        final Spinner spinnerServers = findViewById(R.id.spinnerServers);

        // Liste des serveurs VPN disponibles
        String[] servers = {"Serveur FR - VIP 1", "Serveur DE - Fast", "Serveur US - Unlimited"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, servers);
        spinnerServers.setAdapter(adapter);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected) {
                    tvStatus.setText("Statut : Connecté");
                    tvStatus.setTextColor(Color.GREEN);
                    btnConnect.setText("SÉ DÉCONNECTER");
                    btnConnect.setBackgroundColor(Color.RED);
                    isConnected = true;
                } else {
                    tvStatus.setText("Statut : Déconnecté");
                    tvStatus.setTextColor(Color.RED);
                    btnConnect.setText("SE CONNECTER");
                    btnConnect.setBackgroundColor(Color.parseColor("#0088CC"));
                    isConnected = false;
                }
            }
        });
    }
}
