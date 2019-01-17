/*
 * Copyright (c) 2011-2019, Zingaya, Inc. All rights reserved.
 */

package com.voximplant.foregroundservice;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import static com.voximplant.foregroundservice.Constants.ERROR_INVALID_CONFIG;
import static com.voximplant.foregroundservice.Constants.ERROR_SERVICE_ERROR;
import static com.voximplant.foregroundservice.Constants.NOTIFICATION_CONFIG;

public class VIForegroundServiceModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    public VIForegroundServiceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "VIForegroundService";
    }

    @ReactMethod
    public void createNotificationChannel(ReadableMap channelConfig, Promise promise) {
        if (channelConfig == null) {
            promise.reject(ERROR_INVALID_CONFIG, "VIForegroundService: Channel config is invalid");
            return;
        }
        NotificationHelper.getInstance(getReactApplicationContext()).createNotificationChannel(channelConfig, promise);
    }

    @ReactMethod
    public void startService(ReadableMap notificationConfig, Promise promise) {
        if (notificationConfig == null) {
            promise.reject(ERROR_INVALID_CONFIG, "VIForegroundService: Notification config is invalid");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!notificationConfig.hasKey("channelId")) {
                promise.reject(ERROR_INVALID_CONFIG, "VIForegroundService: channelId is required");
                return;
            }
        }

        if (!notificationConfig.hasKey("id")) {
            promise.reject(ERROR_INVALID_CONFIG , "VIForegroundService: id is required");
            return;
        }

        if (!notificationConfig.hasKey("icon")) {
            promise.reject(ERROR_INVALID_CONFIG, "VIForegroundService: icon is required");
            return;
        }

        if (!notificationConfig.hasKey("title")) {
            promise.reject(ERROR_INVALID_CONFIG, "VIForegroundService: title is reqired");
            return;
        }

        if (!notificationConfig.hasKey("text")) {
            promise.reject(ERROR_INVALID_CONFIG, "VIForegroundService: text is required");
            return;
        }

        Intent intent = new Intent(getReactApplicationContext(), VIForegroundService.class);
        intent.setAction(Constants.ACTION_FOREGROUND_SERVICE_START);
        intent.putExtra(NOTIFICATION_CONFIG, Arguments.toBundle(notificationConfig));
        ComponentName componentName = getReactApplicationContext().startService(intent);
        if (componentName != null) {
            promise.resolve(null);
        } else {
            promise.reject(ERROR_SERVICE_ERROR, "VIForegroundService: Foreground service is not started");
        }
    }

    @ReactMethod
    public void stopService(Promise promise) {
        Intent intent = new Intent(getReactApplicationContext(), VIForegroundService.class);
        intent.setAction(Constants.ACTION_FOREGROUND_SERVICE_STOP);
        boolean stopped = getReactApplicationContext().stopService(intent);
        if (stopped) {
            promise.resolve(null);
        } else {
            promise.reject(ERROR_SERVICE_ERROR, "VIForegroundService: Foreground service failed to stop");
        }
    }
}