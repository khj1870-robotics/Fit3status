package com.fit3.statussms;

import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

public class ReplyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!StatusNotification.ACTION_REPLY.equals(intent.getAction())) return;

        Bundle results = RemoteInput.getResultsFromIntent(intent);
        if (results == null) {
            StatusNotification.show(context);
            return;
        }

        CharSequence value = results.getCharSequence(StatusNotification.KEY_REPLY);
        String message = value == null ? "" : value.toString().trim();
        SharedPreferences prefs = Prefs.get(context);
        String phone = prefs.getString(Prefs.PHONE, "").trim();

        if (phone.isEmpty()) {
            Toast.makeText(context, "앱에서 받을 전화번호를 먼저 저장하세요.", Toast.LENGTH_LONG).show();
            StatusNotification.show(context);
            return;
        }

        if (message.isEmpty()) {
            Toast.makeText(context, "빈 문자는 보내지 않았습니다.", Toast.LENGTH_SHORT).show();
            StatusNotification.show(context);
            return;
        }

        try {
            SmsManager.getDefault().sendTextMessage(phone, null, message, null, null);
            Toast.makeText(context, "상태 문자를 보냈습니다.", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(context, "SMS 권한이 필요합니다.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "문자 전송 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            // Fit3가 답장 후 알림을 읽음 처리하더라도 다시 사용할 수 있도록 갱신한다.
            StatusNotification.show(context);
        }
    }
}
