package android.com.YOLOHealthATM.YOLOKiosk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

class WifiStateReceiver extends BroadcastReceiver {

    private WifiConnector wifiConnector;

    public WifiStateReceiver(WifiConnector wifiConnector) {
        this.wifiConnector = wifiConnector;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (wifiConnector.getWifiStateListener() == null) return;

        int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

        wifiConnector.getWifiStateListener().onStateChange(wifiState);

        switch (wifiState) {

            case WifiManager.WIFI_STATE_ENABLED:
                wifiLog("Wifi enabled");
                wifiConnector.getWifiStateListener().onWifiEnabled();
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                wifiLog("Enabling wifi");
                wifiConnector.getWifiStateListener().onWifiEnabling();
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                wifiLog("Disabling wifi");
                wifiConnector.getWifiStateListener().onWifiDisabling();
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                wifiLog("Wifi disabled");
                wifiConnector.getWifiStateListener().onWifiDisabled();
                break;
        }
    }

    private void wifiLog(String text) {
        if (wifiConnector.isLogOrNot()) Log.d(WifiConnector.TAG, "WifiStateReceiver: " + text);
    }

}