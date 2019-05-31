package android.com.YOLOHealthATM.YOLOKiosk;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.List;

public class WifiActivity extends AppCompatActivity implements WifiConnectorModel {

    Button closePopupBtn, forgetPopupBtn;
    ProgressBar spinner;
    WifiManager wifiManager;
    RecyclerView wifiRecyclerView;

    private WifiListRvAdapter adapter;
    private WifiConnector wifiConnector;

    BroadcastReceiver mShowlist = null;
    IntentFilter mmint = new IntentFilter();
    TextView w;
    public static final String myprefs = "mysp";

    WifiManager wifi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);


        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifi != null;
        if (!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);

        }
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        w = findViewById(R.id.wstatus);

        createWifiConnectorObject();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mShowlist = new ShowWifiListReceiver(wifiConnector);
        registerReceiver(mShowlist, mmint);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mShowlist);

    }

    @Override
    public void createWifiConnectorObject() {
        wifiConnector = new WifiConnector(this);
        wifiConnector.setLog(true);
        wifiConnector.registerWifiStateListener(new WifiStateListener() {
            @Override
            public void onStateChange(int wifiState) {

            }

            @Override
            public void onWifiEnabled() {

            }

            @Override
            public void onWifiEnabling() {

            }

            @Override
            public void onWifiDisabling() {

            }

            @Override
            public void onWifiDisabled() {

            }
        });

        if (!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);
        }
        mmint.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        spinner = findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        closePopupBtn = findViewById(R.id.passwordBtn);
        forgetPopupBtn = findViewById(R.id.forgetPopupBtn);
        wifiRecyclerView = findViewById(R.id.wifiRv);
        forgetPopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiConnector.removeWifiNetwork(wifiConnector.getCurrentWifiSSID(), "");
                adapter.notifyDataSetChanged();
            }
        });
        closePopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(WifiActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        onWifiEnabled();

        adapter = new WifiListRvAdapter(wifiConnector, wifiManager, new WifiListRvAdapter.WifiItemListener() {
            @Override
            public void onWifiItemClicked(ScanResult scanResult) {
                openConnectDialog(scanResult);
            }

            @Override
            public void onWifiItemLongClick(ScanResult scanResult) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        wifiRecyclerView.setLayoutManager(layoutManager);
        wifiRecyclerView.setItemAnimator(new DefaultItemAnimator());
        wifiRecyclerView.setAdapter(adapter);
        wifiRecyclerView.setHasFixedSize(true);
    }

    public void openConnectDialog(ScanResult scanResult) {
        ConnectToWifiDialog dialog = new ConnectToWifiDialog(WifiActivity.this, scanResult);
        dialog.setConnectButtonListener(new ConnectToWifiDialog.DialogListener() {
            @Override
            public void onConnectClicked(ScanResult scanResult, String password) {
                w.setVisibility(View.VISIBLE);
                connectToWifiAccessPoint(scanResult, password);
            }
        });
        dialog.show();
    }

    private void onWifiEnabled() {

        if (permisionLocationOn()) {
            scanForWifiNetworks();
        } else {
            checkLocationTurnOn();
        }
    }

    private Boolean permisionLocationOn() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    private Boolean checkLocationTurnOn() {
        boolean onLocation = true;
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissionGranted) {
            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!gps_enabled) {
                onLocation = false;
                android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Theme_AppCompat_Dialog));
                //android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
                dialog.setMessage("Please turn on your location");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
                dialog.show();
            }
        }
        return onLocation;
    }

    @Override
    public void scanForWifiNetworks() {
        wifiConnector.showWifiList(new ShowWifiListener() {
            @Override
            public void onNetworksFound(WifiManager wifiManager, List<ScanResult> wifiScanResult) {
                if (wifiScanResult.size() > 0) {
                    spinner.setVisibility(View.GONE);
                    adapter.setScanResultList(wifiScanResult);
                }
            }

            @Override
            public void onNetworksFound(JSONArray wifiList) {

            }

            @Override
            public void errorSearchingNetworks(int errorCode) {
                Toast.makeText(WifiActivity.this, "Error on getting wifi list, error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void connectToWifiAccessPoint(final ScanResult scanResult, String password) {
        this.wifiConnector.setScanResult(scanResult, password);
        this.wifiConnector.setLog(true);
        this.wifiConnector.connectToWifi(new ConnectionResultListener() {
            @Override
            public void successfulConnect(String SSID) {
                w.setVisibility(View.INVISIBLE);
                Toast.makeText(WifiActivity.this, "You are connected to " + scanResult.SSID + "!!", Toast.LENGTH_SHORT).show();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void errorConnect(int codeReason) {
                Toast.makeText(WifiActivity.this, "Error on connecting to wifi: " + scanResult.SSID + "\nError code: " + codeReason,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStateChange(SupplicantState supplicantState) {
            }
        });
    }

    @Override
    public void disconnectFromAccessPoint(ScanResult scanResult) {
        wifiConnector.removeWifiNetwork(wifiConnector.getCurrentWifiSSID(), "");
    }

    @Override
    public void destroyWifiConnectorListeners() {
    }

}