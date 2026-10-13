package com.gsmbypasspro.vpn.core;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConfigManager {
    private static final String TAG = "ConfigManager";

    // Structure d'un profil de configuration
    public static class ConfigProfile {
        public String name = "Default Profile";
        public String server = "localhost:22";
        public String payload = "";
        public String hwidLock = "";
        public String expiryDate = "";
        public boolean isLocked = false;
    }

    // Lire et analyser un fichier importé (.gsm, .dark, .json, .txt, .cfg)
    public static ConfigProfile parseConfigFile(Context context, Uri uri) {
        ConfigProfile profile = new ConfigProfile();
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
                // Analyse basique des champs du fichier de config
                if (line.startsWith("SERVER=")) profile.server = line.replace("SERVER=", "").trim();
                else if (line.startsWith("PAYLOAD=")) profile.payload = line.replace("PAYLOAD=", "").trim();
                else if (line.startsWith("HWID=")) profile.hwidLock = line.replace("HWID=", "").trim();
                else if (line.startsWith("EXPIRY=")) profile.expiryDate = line.replace("EXPIRY=", "").trim();
                else if (line.startsWith("LOCKED=")) profile.isLocked = Boolean.parseBoolean(line.replace("LOCKED=", "").trim());
            }
            reader.close();
            if (inputStream != null) inputStream.close();
            Log.d(TAG, "Configuration lue avec succès.");
        } catch (Exception e) {
            Log.e(TAG, "Erreur lecture de config", e);
        }
        return profile;
    }

    // Vérifier si la configuration est valide (HWID et Date d'expiration)
    public static boolean validateConfig(Context context, ConfigProfile profile) {
        // 1. Vérification de la date d'expiration (Format: JJ/MM/AAAA)
        if (profile.expiryDate != null && !profile.expiryDate.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date expiry = sdf.parse(profile.expiryDate);
                Date now = new Date();
                if (expiry != null && now.after(expiry)) {
                    Log.w(TAG, "Configuration expirée !");
                    return false;
                }
            } catch (Exception e) {
                Log.e(TAG, "Erreur format de date", e);
            }
        }

        // 2. Vérification du verrouillage HWID (ID de l'appareil)
        if (profile.hwidLock != null && !profile.hwidLock.isEmpty()) {
            String currentHwid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!profile.hwidLock.equals(currentHwid)) {
                Log.w(TAG, "HWID invalide pour cet appareil !");
                return false;
            }
        }

        return true;
    }
}
