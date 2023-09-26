package com.metris.vibrationfix;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends Activity {
    private AlertDialog alert;
    private boolean dismissForConfigChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide all the graphics because we use the dialog only
        setTitle("");
        overridePendingTransition(0, 0);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_Dialog)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.info_text)
                .setNeutralButton(R.string.accessibility_page, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(this, R.string.activity_not_found, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton(R.string.hide_app, (dialog, which) -> {
                    Toast.makeText(this, R.string.goodbye, Toast.LENGTH_LONG).show();
                    //hide in the standard Android way (supported on Android 10+ because no permissions)
                    MainActivity.this.getPackageManager().setComponentEnabledSetting(
                            new ComponentName(BuildConfig.APPLICATION_ID, BuildConfig.APPLICATION_ID + ".MainAlias"),
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
                })
                .setPositiveButton(R.string.support_me, (dialog, which) ->
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url_store)))))
                .setOnDismissListener((dialog) -> {
                    if (!dismissForConfigChange)
                        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(MainActivity.this::finish, 250);
                    dismissForConfigChange = false;
                });

        alert = builder.create();
        alert.setOnShowListener(dialogInterface -> {
            TextView msg = alert.findViewById(android.R.id.message);
            if (msg != null)
                msg.setMovementMethod(LinkMovementMethod.getInstance());
        });
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alert != null && alert.isShowing()) {
            dismissForConfigChange = true;
            alert.dismiss();
        }
    }
}