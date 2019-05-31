package android.com.YOLOHealthATM.YOLOKiosk;

import android.net.wifi.SupplicantState;

/**
 * Created by jflav on 10/5/2017
 * email: jflavio90@gmail.com
 */
public interface ConnectionResultListener {
    void successfulConnect(String SSID);

    void errorConnect(int codeReason);

    void onStateChange(SupplicantState supplicantState);
}