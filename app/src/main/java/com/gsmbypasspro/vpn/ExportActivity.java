package com.gsmbypasspro.vpn;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ExportActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Créer une configuration .gsm");
        }

        EditText etName = findViewById(R.id.etConfigName);
        EditText etHwid = findViewById(R.id.etHwidLock);
        EditText etExpiry = findViewById(R.id.etExpiryDate);
        CheckBox cbLock = findViewById(R.id.cbLockConfig);
        Button btnSave = findViewById(R.id.btnSaveConfig);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) name = "config_gsm_pro";
            
            String hwid = etHwid.getText().toString().trim();
            String expiry = etExpiry.getText().toString().trim();
            boolean isLocked = cbLock.isChecked();

            String msg = "Fichier '" + name + ".gsm' généré avec succès !";
            if (!hwid.isEmpty()) msg += " [Verrouillé HWID]";
            if (!expiry.isEmpty()) msg += " [Expire le: " + expiry + "]";
            if (isLocked) msg += " [Protégé]";

            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
