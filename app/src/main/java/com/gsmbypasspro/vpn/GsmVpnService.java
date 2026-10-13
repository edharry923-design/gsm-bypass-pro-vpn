package com.gsmbypasspro.vpn;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.IOException;

public class GsmVpnService extends VpnService {

    private static final String TAG = "GsmVpnService";
    private ParcelFileDescriptor mInterface;
    private boolean isRunning = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "STOP".equals(intent.getAction())) {
            stopVpn();
            return START_NOT_STICKY;
        }
        startVpn();
        return START_STICKY;
    }

    private void startVpn() {
        if (isRunning) return;
        
        try {
            // Configuration de l'interface TUN virtuelle
            Builder builder = new Builder();
            builder.setSession("GSM BYPASS-PRO VPN")
                   .addAddress("10.0.0.2", 24)
                   .addDnsServer("1.1.1.1")
                   .addRoute("0.0.0.0", 0); // Route tout le trafic internet du téléphone dans le VPN

            mInterface = builder.establish();
            isRunning = true;
            Log.d(TAG, "Interface VPN TUN établie avec succès.");
        } catch (Exception e) {
            Log.e(TAG, "Erreur lors du démarrage du VPN", e);
        }
    }

    private void stopVpn() {
        try {
            if (mInterface != null) {
                mInterface.close();
                mInterface = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la fermeture du VPN", e);
        }
        isRunning = false;
        stopSelf();
        Log.d(TAG, "VPN arrêté.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopVpn();
    }
}
