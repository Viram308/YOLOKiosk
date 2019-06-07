package android.com.YOLOHealthATM.YOLOKiosk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.com.YOLOHealthATM.YOLOKiosk.thread.StartProxyThread;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button settingsButton;
    WifiP2pManager mManager;

    static int ff = 0;
    static int flag = 0;
    private StartProxyThread proxyThread;
    Button scanWifiBtn;

    Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    TextView status, ssi, pas;
    public static final String myprefs = "mysp";
    String s = "", p = "";
    SharedPreferences sp;
    CountDownTimer ctimer;
    public static String networkName, networkPassword;

    WifiManager wifi;

    private void setLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
        }
    }

    @Override
    public void onBackPressed() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wifi != null;
        if (!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);

        }

        sp = getApplicationContext().getSharedPreferences(myprefs, Context.MODE_PRIVATE);
        proxyThread = new StartProxyThread();
        scanWifiBtn = findViewById(R.id.scanWifi);

        setLocationPermission();


        scanWifiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, WifiActivity.class);
                startActivity(i);

            }
        });

        settingsButton = findViewById(R.id.settingsBtn);

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        status = findViewById(R.id.status);
        ssi = findViewById(R.id.ssid);
        pas = findViewById(R.id.pass);

        status.setText("Waiting for Portable Kit....");
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, MainActivity.this);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                    public void onGroupInfoAvailable(final WifiP2pGroup group) {
                        if (group != null) {
                            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                                @Override
                                public void onSuccess() {
                                    mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("success", "first");
                                            Log.d("ssid1", "" + group.getNetworkName());
                                            Log.d("password1", "" + group.getPassphrase());

                                            proxyThread.start();

                                            flag = 1;
                                            networkName = group.getNetworkName();
                                            networkPassword = group.getPassphrase();
                                            s = sp.getString("ssid", "0");
                                            p = sp.getString("pass", "0");

                                            flag = 1;

                                            if (s.equals("0")) {
                                                Editor edi = sp.edit();
                                                edi.putString("ssid", "" + group.getNetworkName());
                                                edi.putString("pass", "" + group.getPassphrase());
                                                edi.apply();
//                                                ssi.setText("SSID :- " + group.getNetworkName());
//                                                pas.setText("Password :- " + group.getPassphrase());
                                            } else {
                                                ssi.setText("");
                                                pas.setText("");
                                            }
                                        }

                                        @Override
                                        public void onFailure(int reason) {
                                            Log.d("first", "" + reason);
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int reason) {
                                    Log.d("second", "" + reason);
                                }
                            });
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                                        @SuppressLint("Assert")

                                        @Override
                                        public void onSuccess() {
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                                                        public void onGroupInfoAvailable(final WifiP2pGroup group) {

                                                            Log.d("success", "third");
                                                            Log.d("ssid1", "" + group.getNetworkName());

                                                            Log.d("password1", "" + group.getPassphrase());
                                                            proxyThread.start();
                                                            flag = 1;
                                                            networkName = group.getNetworkName();
                                                            networkPassword = group.getPassphrase();

                                                            s = sp.getString("ssid", "0");
                                                            p = sp.getString("pass", "0");



                                                            if (s.equals("0")) {
                                                                Editor edi = sp.edit();
                                                                edi.putString("ssid", "" + group.getNetworkName());
                                                                edi.putString("pass", "" + group.getPassphrase());
                                                                edi.apply();
//                                                                ssi.setText("SSID :- " + group.getNetworkName());
//                                                                pas.setText("Password :- " + group.getPassphrase());
                                                            } else {
                                                                ssi.setText("");
                                                                pas.setText("");
                                                            }
                                                        }
                                                    });
                                                }

                                            }, 2000);
                                        }

                                        @Override
                                        public void onFailure(int reason) {
                                            Toast.makeText(getApplicationContext(), "Not Created", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }, 1000);
                        }
                    }
                });
            }
        }, 6000);
    }

    @Override
    public void onResume() {
        super.onResume();

        ctimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {
                Log.d("tick", "1");
            }

            @Override
            public void onFinish() {
                registerReceiver(mReceiver, mIntentFilter);
                ff = 1;
            }
        };
        ctimer.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        ctimer.cancel();
        ssi.setText("");
        pas.setText("");
        if (ff == 1) {
            unregisterReceiver(mReceiver);
            ff = 0;

        }
    }

}