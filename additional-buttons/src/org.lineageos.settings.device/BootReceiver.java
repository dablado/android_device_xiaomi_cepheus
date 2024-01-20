/*
 * Copyright (C) 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.settings.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "KeyHandler";
    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!context.getSystemService(UserManager.class).isPrimaryUser()) {
            Log.d(TAG, "Not running as the primary user, skipping Keyhandler restoration.");
            return;
        }
        Log.d(KeyHandler.TAG, "KeyHandler: loading prefs");
        AdditionalButtonsActivity.restorePreferences(context);
    }
}
