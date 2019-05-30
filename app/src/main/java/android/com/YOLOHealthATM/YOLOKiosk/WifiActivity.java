package android.com.YOLOHealthATM.YOLOKiosk;

import android.Manifest;
import android.com.YOLOHealthATM.YOLOKiosk.thread.StartProxyThread;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static android.com.YOLOHealthATM.YOLOKiosk.MainActivity.myprefs;

public class WifiActivity extends AppCompatActivity implements WifiConnectorModel {

    Button show_popup, closePopupBtn, enableButton1, disableButton1;
    WifiP2pManager mManager;
    ProgressBar spinner;
    WifiManager wifiManager;
    WifiReceiver wifiReceiver;
    int fl = 0;
    WiFiListAdapter wifiListAdapter;
    ListView wifiListView;
    List<android.net.wifi.ScanResult> wifiList;
    RecyclerView wifiRecyclerView;
    WifiViewHolder wifiViewHolder;

    static int flag=0;
    String[] s1;
    private StartProxyThread proxyThread;
    Button scanWifiBtn, inet;
    private WifiListRvAdapter adapter;
    private WifiConnector wifiConnector;

    private static final String TAG = MainActivity.class.getSimpleName();

    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver, mShowlist = null;
    IntentFilter mIntentFilter, mmint = new IntentFilter();
    TextView status, ssi, pas, w;
    public static final String myprefs = "mysp";
    String s = "", p = "";
    Collection<WifiP2pDevice> l;
    int f = 0;
    LinearLayout linearLayout1;
    SharedPreferences sp;


    private void setMobileDataEnabled(boolean enabled) {
        try {
            TelephonyManager telephonyService = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            Method setMobileDataEnabledMethod = telephonyService.getClass().getDeclaredMethod("setDataEnabled", boolean.class);

            if (null != setMobileDataEnabledMethod) {
                setMobileDataEnabledMethod.invoke(telephonyService, enabled);
            }
        } catch (Exception ex) {
            Log.e(TAG, "Error setting mobile data state", ex);
        }
    }

    // below method returns true if mobile data is on and vice versa
    private boolean mobileDataEnabled(Context context) {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            assert cm != null;
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }
        return mobileDataEnabled;
    }

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

//        if (flag == 0) {
//            sp = getApplicationContext().getSharedPreferences(myprefs, Context.MODE_PRIVATE);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
//                        public void onGroupInfoAvailable(final WifiP2pGroup group) {
//
//                            if (group != null) {
//
//                                mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
//                                    @Override
//                                    public void onSuccess() {
//                                        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
//                                            @Override
//                                            public void onSuccess() {
//                                                Log.d("success", "first");
//                                                Log.d("ssid1", "" + group.getNetworkName());
//                                                Log.d("password1", "" + group.getPassphrase());
//                                                s = sp.getString("ssid", "0");
//                                                p = sp.getString("pass", "0");
//
//
//                                                registerReceiver(mReceiver, mIntentFilter);
//
//                                                if (s.equals("0")) {
//                                                    Editor edi = sp.edit();
//                                                    edi.putString("ssid", "" + group.getNetworkName());
//                                                    edi.putString("pass", "" + group.getPassphrase());
//                                                    edi.apply();
//                                                    ssi.setText("SSID :- " + group.getNetworkName());
//                                                    pas.setText("Password :- " + group.getPassphrase());
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onFailure(int reason) {
//                                                Log.d("first", "" + reason);
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onFailure(int reason) {
//                                        Log.d("second", "" + reason);
//                                    }
//                                });
//                            } else {
//
//                                new Handler().postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
//                                            @SuppressLint("Assert")
//
//                                            @Override
//                                            public void onSuccess() {
//                                                new Handler().postDelayed(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
//                                                            public void onGroupInfoAvailable(final WifiP2pGroup group) {
//
//                                                                Log.d("success", "third");
//                                                                Log.d("ssid1", "" + group.getNetworkName());
//                                                                Log.d("password1", "" + group.getPassphrase());
//                                                                s = sp.getString("ssid", "0");
//                                                                p = sp.getString("pass", "0");
//                                                                registerReceiver(mReceiver, mIntentFilter);
//                                                                if (s.equals("0")) {
//                                                                    Editor edi = sp.edit();
//                                                                    edi.putString("ssid", "" + group.getNetworkName());
//                                                                    edi.putString("pass", "" + group.getPassphrase());
//                                                                    edi.apply();
//                                                                    ssi.setText("SSID :- " + group.getNetworkName());
//                                                                    pas.setText("Password :- " + group.getPassphrase());
//                                                                }
//
//                                                            }
//                                                        });
//                                                    }
//
//                                                }, 2000);
//
//
//                                            }
//
//                                            @Override
//                                            public void onFailure(int reason) {
//                                                Toast.makeText(getApplicationContext(), "Not Created", Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//
//                                    }
//                                }, 1000);
//
//                            }
//
//
//                        }
//                    });
//
//                }
//            }, 1000);
//        }
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


//        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    wifiConnector.enableWifi();
//                } else {
//                    wifiConnector.disableWifi();
//                }
//            }
//        });


            if (!wifi.isWifiEnabled()) {
                wifi.setWifiEnabled(true);
            }
            mmint.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

            spinner = (ProgressBar) findViewById(R.id.progressBar);
            spinner.setVisibility(View.VISIBLE);
            closePopupBtn = (Button) findViewById(R.id.closePopupBtn);
            wifiRecyclerView = findViewById(R.id.wifiRv);

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
                    disconnectFromAccessPoint(scanResult);
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
            //instantiate popup window

//            wifiList = wifiManager.getScanResults();

            //display the popup window
            scanForWifiNetworks();
            //close the popup window on button click
//            wifiListAdapter = new WiFiListAdapter(getApplicationContext(), wifiList);


//            wifiListAdapter.setOnClick(MainActivity.this);


        } else {
            checkLocationTurnOn();
        }
    }

    private void onWifiDisabled() {

//        adapter.setScanResultList(new ArrayList<ScanResult>());
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
