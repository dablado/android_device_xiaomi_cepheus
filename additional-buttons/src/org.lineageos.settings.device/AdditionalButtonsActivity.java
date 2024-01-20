/*
 * Copyright (C) 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.settings.device;

import static org.lineageos.settings.device.SettingsFragment.AI_BUTTON_TORCH;
import static org.lineageos.settings.device.SettingsFragment.SETTINGS_PREFIX;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

public class AdditionalButtonsActivity extends AppCompatActivity {
    private static final String TAG = "KeyHandler";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        if (android.R.id.home == item.getItemId()) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void restorePreferences(final Context context) {
        final SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(context);
        final boolean torch = preferences.getBoolean(AI_BUTTON_TORCH,
                Boolean.FALSE);
        Log.d(TAG, "KeyHandler: loading torch " + torch);

        final ContentResolver resolver = context.getContentResolver();
        Settings.System.putInt(resolver,
                SETTINGS_PREFIX + AI_BUTTON_TORCH, torch ? 1 : 0);
    }
}
