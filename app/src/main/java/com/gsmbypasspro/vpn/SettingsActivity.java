package com.gsmbypasspro.vpn;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Paramètres");
        }

        TextView tvHwid = findViewById(R.id.tvHardwareId);
        String hwid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (tvHwid != null) {
            tvHwid.setText(hwid);
        }

        // Clic sur le sélecteur de thème
        findViewById(R.id.layoutTheme).setOnClickListener(v -> showThemeDialog());
    }

    private void showThemeDialog() {
        String[] themes = {"Système par défaut", "Mode Clair (Light)", "Mode Sombre (Dark)"};
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int currentTheme = prefs.getInt("theme_mode", 2); // Sombre par défaut

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Changer le Thème de l'application");
        builder.setSingleChoiceItems(themes, currentTheme, (dialog, which) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("theme_mode", which);
            editor.apply();

            if (which == 0) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            } else if (which == 1) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }

            dialog.dismiss();
            recreate(); // Applique le thème instantanément
        });
        builder.setNegativeButton("Annuler", null);
        builder.create().show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
