/*
 * Copyright (C) 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.settings.device;

import static android.view.KeyEvent.ACTION_DOWN;
import static org.lineageos.settings.device.Gestures.ACTION_TORCH;
import static org.lineageos.settings.device.SettingsFragment.AI_BUTTON_TORCH;

import android.content.ContentResolver;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.os.DeviceKeyHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KeyHandler implements DeviceKeyHandler {
    public static boolean mTorchEnabled = false;
    public static final String FRONT_CAMERA = "0";
    public static final String TAG = KeyHandler.class.getSimpleName();
    private static final int AI = 689;
    private final EventHandler mEventHandler;
    private final CameraManager mCameraManager;
    private final ExecutorService executorService;
    private final ContentResolver resolver;


    public KeyHandler(final Context context) {
        Log.i(TAG, "Starting KeyHandler");
        this.mEventHandler = new EventHandler(Looper.getMainLooper());
        mCameraManager = context.getSystemService(CameraManager.class);
        mCameraManager.registerTorchCallback(new TorchModeCallback(), mEventHandler);
        executorService = Executors.newSingleThreadExecutor();
        resolver = context.getContentResolver();
    }

    @Override
    public KeyEvent handleKeyEvent(final KeyEvent event) {
        final int scanCode = event.getScanCode();
        final int eventAction = event.getAction();

        if (AI == scanCode && ACTION_DOWN == eventAction) {
            Log.d(TAG, "KeyHandler: AI button pressed");

            try {
                final int torch = Settings.System.getIntForUser(resolver,
                        SettingsFragment.SETTINGS_PREFIX + AI_BUTTON_TORCH,
                        UserHandle.USER_CURRENT);
                Log.d(TAG, "KeyHandler: system setting torch " + torch);
                if (1 == torch) {
                    Log.d(TAG, "KeyHandler: torch enabled");
                    executorService.submit(() -> {
                        final Message message = mEventHandler.obtainMessage(ACTION_TORCH.ordinal());
                        message.arg1 = ACTION_TORCH.ordinal();
                        mEventHandler.handleMessage(message);
                    });
                }
            } catch (Settings.SettingNotFoundException e) {
                Log.d(TAG, "KeyHandler: not found setting");
                return null;
            }

            return null;
        }
        return event;
    }

    private class EventHandler extends Handler {
        public EventHandler(final Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (ACTION_TORCH.ordinal() == msg.arg1) {
                toggleFlashlight();
            }
        }
    }

    private static class TorchModeCallback extends CameraManager.TorchCallback {
        @Override
        public void onTorchModeChanged(String cameraId, boolean enabled) {
            if (!cameraId.equals(FRONT_CAMERA)) {
                return;
            }
            mTorchEnabled = enabled;
        }

        @Override
        public void onTorchModeUnavailable(String cameraId) {
            if (!cameraId.equals(FRONT_CAMERA)) {
                return;
            }
            mTorchEnabled = false;
        }
    }

    private void toggleFlashlight() {
        try {
            mCameraManager.setTorchMode(FRONT_CAMERA, !mTorchEnabled);
            mTorchEnabled = !mTorchEnabled;
        } catch (CameraAccessException e) {
            Log.e(TAG, "KeyHandler: CameraAccessException");
        }
    }
}
