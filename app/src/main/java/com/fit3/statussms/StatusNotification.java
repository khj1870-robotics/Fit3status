package com.fit3.statussms;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Person;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;

final class StatusNotification {
    static final String CHANNEL_ID = "fit3_status_sender";
    private static final int FIRST_NOTIFICATION_ID = 3103;
    private static final int SECOND_NOTIFICATION_ID = 3104;
    private static final String PREFS_NAME = "fit3_notification_state";
    private static final String KEY_NOTIFICATION_ID = "notification_id";

    static final String KEY_REPLY = "status_reply";
    static final String ACTION_REPLY = "com.fit3.statussms.SEND_REPLY";
    static final String ACTION_NOTIFICATION_DISMISSED =
            "com.fit3.statussms.NOTIFICATION_DISMISSED";

    private StatusNotification() {}

    static void show(Context context) {
        show(context, false);
    }

    static void showAsNewNotification(Context context) {
        show(context, true);
    }

    private static void show(Context context, boolean useNewId) {
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        createChannel(manager);

        int notificationId = getNotificationId(context, useNewId);

        Intent replyIntent = new Intent(context, ReplyReceiver.class)
                .setAction(ACTION_REPLY);
        PendingIntent replyPendingIntent = PendingIntent.getBroadcast(
                context,
                100,
                replyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE
        );

        RemoteInput remoteInput = new RemoteInput.Builder(KEY_REPLY)
                .setLabel("보낼 상태 선택")
                .build();

        Notification.Action replyAction = new Notification.Action.Builder(
                android.R.drawable.ic_menu_send,
                "상태 보내기",
                replyPendingIntent
        ).addRemoteInput(remoteInput).build();

        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent openPendingIntent = PendingIntent.getActivity(
                context,
                101,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Intent dismissedIntent = new Intent(context, NotificationDismissedReceiver.class)
                .setAction(ACTION_NOTIFICATION_DISMISSED);
        PendingIntent dismissedPendingIntent = PendingIntent.getBroadcast(
                context,
                102,
                dismissedIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Person sender = new Person.Builder().setName("상태 보내기").build();
        Notification.MessagingStyle style = new Notification.MessagingStyle(sender)
                .setConversationTitle("연인에게 상태 문자 보내기")
                .addMessage("알림을 열고 빠른 답장을 선택하세요.",
                        System.currentTimeMillis(), sender);

        Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle("연인에게 상태 보내기")
                .setContentText("알림을 열고 빠른 답장을 선택하세요")
                .setStyle(style)
                .setContentIntent(openPendingIntent)
                .setDeleteIntent(dismissedPendingIntent)
                .addAction(replyAction)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setOngoing(false)
                .setAutoCancel(false)
                .setOnlyAlertOnce(false)
                .setColor(Color.rgb(79, 99, 86))
                .setVisibility(Notification.VISIBILITY_PRIVATE)
                .build();

        manager.notify(notificationId, notification);
    }

    static void hide(Context context) {
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.cancel(FIRST_NOTIFICATION_ID);
        manager.cancel(SECOND_NOTIFICATION_ID);
    }

    static boolean notificationsAllowed(Context context) {
        return context.getSystemService(NotificationManager.class).areNotificationsEnabled();
    }

    static void openNotificationSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName())
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static int getNotificationId(Context context, boolean useNewId) {
        android.content.SharedPreferences prefs = context.getSharedPreferences(
                PREFS_NAME, Context.MODE_PRIVATE);
        int currentId = prefs.getInt(KEY_NOTIFICATION_ID, FIRST_NOTIFICATION_ID);

        if (!useNewId) {
            return currentId;
        }

        int nextId = currentId == FIRST_NOTIFICATION_ID
                ? SECOND_NOTIFICATION_ID
                : FIRST_NOTIFICATION_ID;
        prefs.edit().putInt(KEY_NOTIFICATION_ID, nextId).apply();
        return nextId;
    }

    private static void createChannel(NotificationManager manager) {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Fit3 상태 보내기",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription("Galaxy Fit3에서 상태 문자를 보내기 위한 알림");
        channel.setSound(null, null);
        channel.enableVibration(false);
        channel.setShowBadge(false);
        manager.createNotificationChannel(channel);
    }
}
