package com.hussain.UrDataUsage.view;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.hussain.UrDataUsage.R;
import com.hussain.UrDataUsage.utils.NetworkStatsHelper;
import com.hussain.UrDataUsage.utils.PackageManagerHelper;
import com.hussain.UrDataUsage.utils.TrafficStatsHelper;

public class StatsActivity extends AppCompatActivity {
    private static final int READ_PHONE_STATE_REQUEST = 37;
    public static final String EXTRA_PACKAGE = "ExtraPackage";


    AppCompatImageView ivIcon;
    Toolbar toolbar;

    TextView trafficStatsAllRx;
    TextView trafficStatsAllTx;
    TextView trafficStatsPackageRx;
    TextView trafficStatsPackageTx;

    TextView networkStatsAllRx;
    TextView networkStatsAllTx;
    TextView networkStatsPackageRx;
    TextView networkStatsPackageTx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ivIcon = (AppCompatImageView) findViewById(R.id.avatar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestPermissions();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onResume() {
        super.onResume();
        if (!hasPermissions()) {
            return;
        }
        initTextViews();
        checkIntent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras == null) {
            return;
        }
        String packageName = extras.getString(EXTRA_PACKAGE);
        if (packageName == null) {
            return;
        }
        try {
            ivIcon.setImageDrawable(getPackageManager().getApplicationIcon(packageName));
            toolbar.setTitle(getPackageManager().getApplicationLabel(
                    getPackageManager().getApplicationInfo(
                            packageName, PackageManager.GET_META_DATA)));
            toolbar.setSubtitle(packageName);
            setSupportActionBar(toolbar);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (!PackageManagerHelper.isPackage(StatsActivity.this, packageName)) {
            return;
        }
        fillData(packageName);
    }

    private void requestPermissions() {
        if (!hasPermissionToReadNetworkHistory()) {
            return;
        }
        if (!hasPermissionToReadPhoneStats()) {
            requestPhoneStateStats();
        }
    }

    private boolean hasPermissions() {
        return hasPermissionToReadNetworkHistory() && hasPermissionToReadPhoneStats();
    }

    private void initTextViews() {

        networkStatsAllRx = (TextView) findViewById(R.id.network_stats_all_rx_value);
        networkStatsAllTx = (TextView) findViewById(R.id.network_stats_all_tx_value);
        networkStatsPackageRx = (TextView) findViewById(R.id.network_stats_package_rx_value);
        networkStatsPackageTx = (TextView) findViewById(R.id.network_stats_package_tx_value);
    }

    private void fillData(String packageName) {
        int uid = PackageManagerHelper.getPackageUid(this, packageName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkStatsManager networkStatsManager = (NetworkStatsManager) getApplicationContext().getSystemService(Context.NETWORK_STATS_SERVICE);
            NetworkStatsHelper networkStatsHelper = new NetworkStatsHelper(networkStatsManager, uid);
            fillNetworkStatsAll(networkStatsHelper);
            fillNetworkStatsPackage(uid, networkStatsHelper);
        }
        else{
            fillTrafficStatsAll();

        fillTrafficStatsPackage(uid);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void fillNetworkStatsAll(NetworkStatsHelper networkStatsHelper) {
        long mobileRx = networkStatsHelper.getAllRxBytesMobile(this);
        mobileRx=mobileRx/1024;
        mobileRx=mobileRx/1024;
        mobileRx=mobileRx/1024;
        networkStatsAllRx.setText(mobileRx + " Gb");
        long mobileTx = networkStatsHelper.getAllTxBytesMobile(this);
        mobileTx=mobileTx/1024;
        mobileTx=mobileTx/1024;
        mobileTx=mobileTx/1024;
        networkStatsAllTx.setText(mobileTx + " Gb");
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void fillNetworkStatsPackage(int uid, NetworkStatsHelper networkStatsHelper) {
        long mobileRx = networkStatsHelper.getPackageRxBytesMobile(this);
        mobileRx=mobileRx/1024;
        mobileRx=mobileRx/1024;
        mobileRx=mobileRx/1024;
        networkStatsPackageRx.setText(mobileRx + " Gb");
        long mobileTx = networkStatsHelper.getPackageTxBytesMobile(this);
        mobileTx=mobileTx/1024;
        mobileTx=mobileTx/1024;
        mobileTx=mobileTx/1024;
        networkStatsPackageTx.setText(mobileTx + " Gb");
    }

    private void fillTrafficStatsAll() {
        long mobileRx = TrafficStatsHelper.getAllRxBytes();
        mobileRx=mobileRx/1024;
        mobileRx=mobileRx/1024;
        mobileRx=mobileRx/1024;
        networkStatsAllRx.setText(mobileRx + " Gb");
        long mobileTx = TrafficStatsHelper.getAllTxBytes();
        mobileTx=mobileTx/1024;
        mobileTx=mobileTx/1024;
        mobileTx=mobileTx/1024;
        networkStatsAllTx.setText(mobileTx + " Gb");
    }

    private void fillTrafficStatsPackage(int uid) {
        long mobileRx = TrafficStatsHelper.getPackageRxBytes(uid);
        mobileRx=mobileRx/1024;
        mobileRx=mobileRx/1024;
        mobileRx=mobileRx/1024;
        networkStatsPackageRx.setText(mobileRx + " Gb");
        long mobileTx = TrafficStatsHelper.getPackageTxBytes(uid);
        mobileTx=mobileTx/1024;
        mobileTx=mobileTx/1024;
        mobileTx=mobileTx/1024;
        networkStatsPackageTx.setText(mobileTx + " Gb");
    }

    private boolean hasPermissionToReadPhoneStats() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
            return false;
        } else {
            return true;
        }
    }

    private void requestPhoneStateStats() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_STATE_REQUEST);
    }

    private boolean hasPermissionToReadNetworkHistory() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        final AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        if (mode == AppOpsManager.MODE_ALLOWED) {
            return true;
        }
        appOps.startWatchingMode(AppOpsManager.OPSTR_GET_USAGE_STATS,
                getApplicationContext().getPackageName(),
                new AppOpsManager.OnOpChangedListener() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.M)
                    public void onOpChanged(String op, String packageName) {
                        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                                android.os.Process.myUid(), getPackageName());
                        if (mode != AppOpsManager.MODE_ALLOWED) {
                            return;
                        }
                        appOps.stopWatchingMode(this);
                        Intent intent = new Intent(StatsActivity.this, StatsActivity.class);
                        if (getIntent().getExtras() != null) {
                            intent.putExtras(getIntent().getExtras());
                        }
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplicationContext().startActivity(intent);
                    }
                });
        requestReadNetworkHistoryAccess();
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void requestReadNetworkHistoryAccess() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }
}
