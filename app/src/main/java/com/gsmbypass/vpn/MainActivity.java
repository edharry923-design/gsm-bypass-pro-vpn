package com.gsmbypass.vpn;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    private static final int VPN_REQUEST_CODE = 0x0F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Button button = new Button(this);
        button.setText("Connecter GSM BYPASS-PRO VPN");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = VpnService.prepare(MainActivity.this);
                if (intent != null) {
                    startActivityForResult(intent, VPN_REQUEST_CODE);
                } else {
                    onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
                }
            }
        });
        setContentView(button);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, VpnServiceProxy.java);
            startService(intent);
        }
    }
}
