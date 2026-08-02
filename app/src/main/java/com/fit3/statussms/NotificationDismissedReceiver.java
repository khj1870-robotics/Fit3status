package com.fit3.statussms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

public class NotificationDismissedReceiver extends BroadcastReceiver {
    private static final long REPOST_DELAY_MS = 1000L;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!StatusNotification.ACTION_NOTIFICATION_DISMISSED.equals(intent.getAction())
                || !StatusNotification.notificationsAllowed(context)) {
            return;
        }

        PendingResult pendingResult = goAsync();
        Context appContext = context.getApplicationContext();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            try {
                StatusNotification.showAsNewNotification(appContext);
            } finally {
                pendingResult.finish();
            }
        }, REPOST_DELAY_MS);
    }
}
