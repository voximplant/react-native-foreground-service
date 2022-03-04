package com.voximplant.foregroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ForegroundReceiver extends BroadcastReceiver {
    IBroadcastListener subscriber;

    public void addListener(IBroadcastListener listener) {
        this.subscriber = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.subscriber.buttonPressedEvent();
    }
}
