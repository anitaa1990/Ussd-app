package com.an.ussdapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;

import com.an.deviceinfo.permission.PermissionManager;
import com.an.deviceinfo.permission.PermissionUtils;
import com.an.ussdapp.R;
import com.an.ussdapp.TextUtils;
import com.an.ussdapp.utils.BaseUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public abstract class PermissionActivity extends AppCompatActivity implements PermissionManager.PermissionCallback {

    protected PermissionManager permissionManager;
    protected PermissionUtils permissionUtils;

    public enum PERMISSION {
        PHONE(Manifest.permission.READ_PHONE_STATE),
        LOCATION(Manifest.permission.ACCESS_FINE_LOCATION);

        private final String permission;
        PERMISSION(String permission) {
            this.permission = permission;
        }

        protected String getPermission() {
            return permission;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionManager = new PermissionManager(this);
        permissionUtils = new PermissionUtils(this);
    }


    protected boolean isPermissionsGranted() {
        if (!permissionUtils.isPermissionGranted(PERMISSION.PHONE.getPermission())
                || ! permissionUtils.isPermissionGranted(PERMISSION.LOCATION.getPermission())) {
            return false;
        }
        return true;
    }

    protected void askPermissions() {
        if (!permissionUtils.isPermissionGranted(PERMISSION.PHONE.getPermission())) {
            permissionManager.showPermissionDialog(PERMISSION.PHONE.getPermission())
                    .withCallback(this)
                    .withDenyDialogEnabled(true)
                    .withDenyDialogMsg(TextUtils.getString(getApplicationContext(), String.format("permission_%s", PERMISSION.PHONE.name().toLowerCase())))
                    .build();

        } else if (!permissionUtils.isPermissionGranted(PERMISSION.LOCATION.getPermission())) {
            permissionManager.showPermissionDialog(PERMISSION.LOCATION.getPermission())
                    .withCallback(this)
                    .withDenyDialogEnabled(true)
                    .withDenyDialogMsg(TextUtils.getString(getApplicationContext(), String.format("permission_%s", PERMISSION.LOCATION.name().toLowerCase())))
                    .build();
        }
    }


    protected void askAccessibilityPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name).setMessage(getString(R.string.permission_accessibility));

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BaseUtils.openAccessibilitySettings(PermissionActivity.this);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    protected boolean isAccessibilityGranted() {
        return BaseUtils.isAccessibilityEnabled(getApplicationContext());
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionManager.handleResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onPermissionDismissed(String permission) {

    }

    @Override
    public void onPositiveButtonClicked(DialogInterface dialog, int which) {
        BaseUtils.openAppSettings(this);
    }

    @Override
    public void onNegativeButtonClicked(DialogInterface dialog, int which) {
        finish();
    }
}
