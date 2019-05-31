package android.com.YOLOHealthATM.YOLOKiosk;

import android.net.wifi.ScanResult;

public interface WifiConnectorModel {

    void createWifiConnectorObject();

    void scanForWifiNetworks();

    void connectToWifiAccessPoint(ScanResult scanResult, String password);
}
