package com.fit3.statussms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationDismissedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (StatusNotification.ACTION_NOTIFICATION_DISMISSED.equals(intent.getAction())
                && StatusNotification.notificationsAllowed(context)) {
            StatusNotification.show(context.getApplicationContext());
        }
    }
}
