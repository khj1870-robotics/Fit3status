package com.fit3.statussms;

import android.content.Context;
import android.content.SharedPreferences;

final class Prefs {
    private static final String FILE = "settings";
    static final String PHONE = "phone";
    static final String ENABLED = "enabled";

    private Prefs() {}

    static SharedPreferences get(Context context) {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }
}
