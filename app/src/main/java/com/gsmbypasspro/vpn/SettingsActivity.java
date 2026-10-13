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

        TextView tvHwid = findViewById(R.id.tvHardwareId);
        String hwid = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        if (tvHwid != null) {
            tvHwid.setText(hwid);
        }

        findViewById(R.id.layoutTheme).setOnClickListener(v -> showThemeDialog());
    }

    private void showThemeDialog() {
        String[] themes = {"Système", "Clair", "Sombre"};
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        int currentTheme = prefs.getInt("theme_mode", 2); // 2 par défaut (Dark)

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choisir le Thème");
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
            recreate();
        });
        builder.setNegativeButton("Annuler", null);
        builder.create().show();
    }
}
