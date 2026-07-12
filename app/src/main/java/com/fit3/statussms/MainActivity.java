package com.fit3.statussms;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int REQUEST_PERMISSIONS = 10;
    private EditText phoneInput;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(buildUi());
        refreshStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshStatus();
    }

    private View buildUi() {
        int pad = dp(24);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(pad, pad, pad, pad);
        content.setBackgroundColor(Color.rgb(247, 247, 242));

        TextView title = text("Fit3 상태 문자", 26, Color.rgb(30, 35, 31));
        content.addView(title);

        TextView description = text(
                "Galaxy Fit3의 고정 알림에서 빠른 답장을 고르면 아래 번호로 SMS를 보냅니다.",
                16,
                Color.DKGRAY
        );
        description.setPadding(0, dp(12), 0, dp(20));
        content.addView(description);

        TextView phoneLabel = text("받을 사람 전화번호", 15, Color.DKGRAY);
        content.addView(phoneLabel);

        phoneInput = new EditText(this);
        phoneInput.setHint("01012345678");
        phoneInput.setInputType(InputType.TYPE_CLASS_PHONE);
        phoneInput.setText(Prefs.get(this).getString(Prefs.PHONE, ""));
        content.addView(phoneInput, matchWrap());

        Button enableButton = button("저장하고 고정 알림 켜기");
        enableButton.setOnClickListener(v -> enable());
        content.addView(enableButton, withTopMargin(16));

        Button disableButton = button("고정 알림 끄기");
        disableButton.setOnClickListener(v -> {
            Prefs.get(this).edit().putBoolean(Prefs.ENABLED, false).apply();
            StatusNotification.hide(this);
            refreshStatus();
        });
        content.addView(disableButton, withTopMargin(8));

        Button notificationSettings = button("알림 설정 열기");
        notificationSettings.setOnClickListener(v -> StatusNotification.openNotificationSettings(this));
        content.addView(notificationSettings, withTopMargin(8));

        statusText = text("", 14, Color.DKGRAY);
        statusText.setPadding(0, dp(20), 0, dp(12));
        content.addView(statusText);

        TextView guide = text(
                "Fit3 설정\n"
                        + "1. Galaxy Wearable → 밴드 설정 → 알림 → 앱 알림에서 ‘Fit3 상태 문자’를 켭니다.\n"
                        + "2. Fit3에서 고정 알림을 열고 ‘상태 보내기’를 누릅니다.\n"
                        + "3. 빠른 응답 문구 하나를 선택합니다.\n\n"
                        + "빠른 응답 문구는 Galaxy Wearable의 빠른 응답 관리 화면에서 편집합니다.",
                15,
                Color.rgb(60, 66, 61)
        );
        guide.setLineSpacing(0, 1.2f);
        content.addView(guide);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(content);
        return scroll;
    }

    private void enable() {
        String phone = phoneInput.getText().toString().replace(" ", "").replace("-", "").trim();
        if (phone.length() < 8) {
            Toast.makeText(this, "전화번호를 확인하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        Prefs.get(this).edit()
                .putString(Prefs.PHONE, phone)
                .putBoolean(Prefs.ENABLED, true)
                .apply();

        if (checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.SEND_SMS, Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_PERMISSIONS
            );
        } else {
            StatusNotification.show(this);
            refreshStatus();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) allGranted &= result == PackageManager.PERMISSION_GRANTED;
            if (allGranted) {
                StatusNotification.show(this);
            } else {
                Toast.makeText(this, "알림과 SMS 권한을 모두 허용해야 합니다.", Toast.LENGTH_LONG).show();
            }
            refreshStatus();
        }
    }

    private void refreshStatus() {
        if (statusText == null) return;
        boolean enabled = Prefs.get(this).getBoolean(Prefs.ENABLED, false);
        statusText.setText(enabled && StatusNotification.notificationsAllowed(this)
                ? "상태: 고정 알림 사용 중"
                : "상태: 꺼짐 또는 권한 확인 필요");
    }

    private TextView text(String value, int sizeSp, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(sizeSp);
        view.setTextColor(color);
        return view;
    }

    private Button button(String value) {
        Button button = new Button(this);
        button.setText(value);
        button.setAllCaps(false);
        return button;
    }

    private LinearLayout.LayoutParams matchWrap() {
        return new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
    }

    private LinearLayout.LayoutParams withTopMargin(int dp) {
        LinearLayout.LayoutParams params = matchWrap();
        params.topMargin = dp(dp);
        return params;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
