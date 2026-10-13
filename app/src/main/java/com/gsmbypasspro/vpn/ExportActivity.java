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
            getSupportActionBar().setTitle("Exporter la configuration");
        }

        EditText etName = findViewById(R.id.etConfigName);
        Button btnSave = findViewById(R.id.btnSaveConfig);

        btnSave.setOnClickListener(v -> {
            String configName = etName.getText().toString().trim();
            if (configName.isEmpty()) {
                configName = "config_gsm_pro";
            }
            Toast.makeText(this, "Configuration " + configName + ".gsm exportée !", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
