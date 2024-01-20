package org.lineageos.settings.device;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {
    public static final String AI_BUTTON_TORCH = "ai_button_torch";
    public static final String SETTINGS_PREFIX = "key_handler_";
    private static final String TAG = "KeyHandler";
    private SharedPreferences preferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        loadPreferences();
    }

    private void loadPreferences() {
        final SwitchPreferenceCompat preference = findPreference(AI_BUTTON_TORCH);

        if (preference != null) {
            Log.d(TAG, "KeyHandler: found preference");
            final Context context = requireContext();
            preferences = PreferenceManager.getDefaultSharedPreferences(context);

            final boolean torch = preferences.getBoolean(AI_BUTTON_TORCH, Boolean.FALSE);
            preference.setChecked(torch);
            preference.setOnPreferenceChangeListener(this);

            Log.d(TAG, "KeyHandler: loading torch " + torch);

            KeyHandler.mTorchEnabled = torch;

            final ContentResolver resolver = context.getContentResolver();
            Settings.System.putIntForUser(resolver,
                    SETTINGS_PREFIX + AI_BUTTON_TORCH, torch ? 1 : 0,
                    UserHandle.USER_CURRENT
            );
        } else {
            Log.e(TAG, "KeyHandler: SwitchPreferenceCompat is null. Unable to proceed.");
        }
    }

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        final Boolean torch = (Boolean) newValue;

        final SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(AI_BUTTON_TORCH, torch);
        edit.apply();

        Log.d(TAG, "KeyHandler: updating torch: " + torch);
        final ContentResolver resolver = requireContext().getContentResolver();
        Settings.System.putIntForUser(resolver,
                SETTINGS_PREFIX + AI_BUTTON_TORCH, torch ? 1 : 0, UserHandle.USER_CURRENT);

        return true;
    }

}
