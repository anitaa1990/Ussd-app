package com.an.ussdapp.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.an.ussdapp.service.CustomAccessibilityService;
import com.an.ussdapp.adapter.NetworkAdapter;
import com.an.ussdapp.R;
import com.an.ussdapp.utils.BaseUtils;
import com.an.ussdapp.utils.TextUtils;
import com.an.ussdapp.databinding.MainActivityBinding;
import com.an.ussdapp.views.RecyclerItemClickListener;

import java.util.Arrays;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;


//Step 1: when app opens, display list of ussd codes
//Step 2: when one of the items in the list is clicked, then ask for phone & location permission.
//Step 3: once permission is given, ask for accessibility permission.
//Step 4: if permission is given, initiate ussd code
//Step 5 display the incoming ussd code in a toast message.
public class MainActivity extends PermissionActivity implements RecyclerItemClickListener.OnItemClickListener {


    private Intent serviceIntent;
    private BroadcastReceiver notifyBroadcastReceiver;

    private String selectedUssd;
    private NetworkAdapter adapter;
    private MainActivityBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        adapter = new NetworkAdapter(getApplicationContext(), Arrays.asList(getResources().getStringArray(R.array.network_list)));
        binding.listFeed.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.listFeed.setAdapter(adapter);
        binding.listFeed.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), this));

        serviceIntent = new Intent(getApplicationContext(), CustomAccessibilityService.class);
        startService(serviceIntent);
        registerNotificationReceiver();
    }


    private void registerNotificationReceiver() {
        notifyBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BaseUtils.showToast(getApplicationContext(), String.format(getString(R.string.response), intent.getStringExtra("message")));
            }
        };
        registerReceiver(notifyBroadcastReceiver, new IntentFilter("com.an.ussdapp.action.REFRESH"));
    }


    @Override
    public void onItemClick(View view, int position) {
        selectedUssd = TextUtils.getString(getApplicationContext(), String.format("ussd_%d", position + 1));
        handlePermissions();
    }


    private void handlePermissions() {
        if (!isPermissionsGranted()) askPermissions();
        else handleAccessibilityServices();
    }


    private void handleAccessibilityServices() {
        if (!isAccessibilityGranted()) {
            askAccessibilityPermission();
        } else {
            BaseUtils.sendUSSDRequest(this, selectedUssd);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceIntent != null) {
            stopService(serviceIntent);
            serviceIntent = null;
        }
        if (notifyBroadcastReceiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(notifyBroadcastReceiver);
    }


    @Override
    public void onPermissionGranted(String[] strings, int[] ints) {
        handlePermissions();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BaseUtils.SETTINGS_REQUEST_CODE) {
            if (isPermissionsGranted()) {
                handleAccessibilityServices();
            }

        } else if (requestCode == BaseUtils.ACCESSIBILITY_REQUEST_CODE) {
            handleAccessibilityServices();
        }
    }
}
