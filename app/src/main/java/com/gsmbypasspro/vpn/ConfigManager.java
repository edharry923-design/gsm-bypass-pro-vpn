package com.gsmbypasspro.vpn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.os.Build;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConfigManager {

    @SuppressLint("HardwareIds")
    public static String getHardwareId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (androidId == null || androidId.isEmpty()) {
            androidId = "GSM_PRO_" + Build.BOARD + "_" + Build.SERIAL;
        }
        return androidId;
    }

    public static boolean isExpired(String expiryDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date expiryDate = sdf.parse(expiryDateStr);
            Date currentDate = new Date();
            if (expiryDate != null) {
                return currentDate.after(expiryDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isDeviceAuthorized(Context context, String targetHardwareId) {
        if (targetHardwareId == null || targetHardwareId.equals("ANY") || targetHardwareId.isEmpty()) {
            return true;
        }
        String currentDevice = getHardwareId(context);
        return currentDevice.equals(targetHardwareId);
    }
}
