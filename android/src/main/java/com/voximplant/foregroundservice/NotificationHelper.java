/*
 * Copyright (c) 2011-2019, Zingaya, Inc. All rights reserved.
 */

package com.voximplant.foregroundservice;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;

import java.util.ArrayList;

import static com.voximplant.foregroundservice.Constants.ERROR_ANDROID_VERSION;
import static com.voximplant.foregroundservice.Constants.ERROR_INVALID_CONFIG;

class NotificationHelper {
    private static NotificationHelper instance = null;
    private NotificationManager mNotificationManager;

    public static synchronized NotificationHelper getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationHelper(context);
        }
        return instance;
    }

    private static ReactContext reactContext;
    private static PendingIntent notifIntent;

    private NotificationHelper(Context context) {
        mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    void createNotificationChannel(ReadableMap channelConfig, Promise promise, ReactContext reactContextParam) {
        reactContext = reactContextParam;
        if (channelConfig == null) {
            Log.e("NotificationHelper", "createNotificationChannel: invalid config");
            promise.reject(ERROR_INVALID_CONFIG, "VIForegroundService: Channel config is invalid");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!channelConfig.hasKey("id")) {
                promise.reject(ERROR_INVALID_CONFIG, "VIForegroundService: Channel id is required");
                return;
            }
            String channelId = channelConfig.getString("id");
            if (!channelConfig.hasKey("name")) {
                promise.reject(ERROR_INVALID_CONFIG, "VIForegroundService: Channel name is required");
                return;
            }
            String channelName = channelConfig.getString("name");
            String channelDescription = channelConfig.getString("description");
            int channelImportance = channelConfig.hasKey("importance") ?
                    channelConfig.getInt("importance") : NotificationManager.IMPORTANCE_LOW;
            boolean enableVibration = channelConfig.hasKey("enableVibration") && channelConfig.getBoolean("enableVibration");
            if (channelId == null || channelName == null) {
                promise.reject(ERROR_INVALID_CONFIG, "VIForegroundService: Channel id or name is not specified");
                return;
            }
            NotificationChannel channel = new NotificationChannel(channelId, channelName, channelImportance);
            channel.setDescription(channelDescription);
            channel.enableVibration(enableVibration);
            mNotificationManager.createNotificationChannel(channel);
            promise.resolve(null);
        } else {
            promise.reject(ERROR_ANDROID_VERSION, "VIForegroundService: Notification channel can be created on Android O+");
        }

    }

    Notification buildNotification(Context context, Bundle notificationConfig) {
        if (notificationConfig == null) {
            Log.e("NotificationHelper", "buildNotification: invalid config");
            return null;
        }
        Class mainActivityClass = getMainActivityClass(context);
        if (mainActivityClass == null) {
            return null;
        }
        Intent notificationIntent = new Intent(context, mainActivityClass);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notifIntent = pendingIntent;
        Notification.Builder notificationBuilder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = notificationConfig.getString("channelId");
            if (channelId == null) {
                Log.e("NotificationHelper", "buildNotification: invalid channelId");
                return null;
            }
            notificationBuilder = new Notification.Builder(context, channelId);
        } else {
            notificationBuilder = new Notification.Builder(context);
        }

        int priorityInt = notificationConfig.containsKey("priority") ? notificationConfig.getInt("priority"): Notification.PRIORITY_HIGH;

        int priority;
        switch (priorityInt) {
            case 0:
                priority = Notification.PRIORITY_DEFAULT;
                break;
            case -1:
                priority = Notification.PRIORITY_LOW;
                break;
            case -2:
                priority = Notification.PRIORITY_MIN;
                break;
            case 1:
                priority = Notification.PRIORITY_HIGH;
                break;
            case 2:
                priority = Notification.PRIORITY_MAX;
                break;
            default:
                priority = Notification.PRIORITY_HIGH;
                break;

        }

        notificationBuilder.setContentTitle(notificationConfig.getString("title"))
                .setContentText(notificationConfig.getString("text"))
                .setPriority(priority)
                .setContentIntent(pendingIntent);

        String iconName = notificationConfig.getString("icon");
        if (iconName != null) {
            notificationBuilder.setSmallIcon(getResourceIdForResourceName(context, iconName));
        }

        if(notificationConfig.containsKey("actionButtons")){
            ArrayList<Bundle> buttons = (ArrayList<Bundle>) notificationConfig.get("actionButtons");

            int numButtons = buttons.size();
            ArrayList<Intent> buttonIntents = new ArrayList<Intent>();
            ArrayList<PendingIntent> pendingButtonIntents = new ArrayList<PendingIntent>();

            for(int i=0; i < numButtons; i++){
                Bundle button = buttons.get(i);
                String buttonLabel = button.getString("label");
                String actionLabel = button.getString("actionLabel");
                boolean redirect = button.getBoolean("redirect");
                buttonIntents.add(i, new Intent(context, NotificationBroadcastReceiver.class));

                buttonIntents.get(i).putExtra("actionLabel", actionLabel);
                buttonIntents.get(i).putExtra("redirect", redirect);

                pendingButtonIntents.add(i, PendingIntent.getBroadcast(context,i+1, buttonIntents.get(i), PendingIntent.FLAG_UPDATE_CURRENT));

                notificationBuilder.addAction(0, buttonLabel, pendingButtonIntents.get(i));
            }
            Log.d("TAG", "buildNotification: ");
        }



        return notificationBuilder.build();
    }

    private Class getMainActivityClass(Context context) {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent == null || launchIntent.getComponent() == null) {
            Log.e("NotificationHelper", "Failed to get launch intent or component");
            return null;
        }
        try {
            return Class.forName(launchIntent.getComponent().getClassName());
        } catch (ClassNotFoundException e) {
            Log.e("NotificationHelper", "Failed to get main activity class");
            return null;
        }
    }

    public static class NotificationBroadcastReceiver extends BroadcastReceiver {
        public NotificationBroadcastReceiver(){}
        private void sendEvent(ReactContext reactContext,
                               String eventName,
                               @Nullable WritableMap params) {
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            WritableMap params = Arguments.createMap();
            boolean redirect = intent.getBooleanExtra("redirect", false);
            String actionLabel = intent.getStringExtra("actionLabel");
            params.putString("actionLabel", actionLabel);
            sendEvent(reactContext, "ActionButtonPress", params);
                if(redirect){
                    try {
                        notifIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }


        }
    }

    private int getResourceIdForResourceName(Context context, String resourceName) {
        int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
        if (resourceId == 0) {
            resourceId = context.getResources().getIdentifier(resourceName, "mipmap", context.getPackageName());
        }
        return resourceId;
    }
}