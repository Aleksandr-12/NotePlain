package com.writesimple.simplenote.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    private static final String APP_PREFERENCES = "shared";
    SharedPreferences sharedForDelete;
    SharedPreferences sharedForUpdate;
    String valueDeleteSettings = "delete";
    String valueUpdateSettings = "update";
    String valueMainDeleteSettings = "MainDelete";
    String valueMainUpdateSettings = "MainUpdate";

    public String loadSharedSettings(SharedPreferences shared, String name) {
        shared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        return shared.getString(name, "");
    }

    public void saveSharedValueSettings(SharedPreferences shared,String name,String value) {
        shared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editShareSort = shared.edit();
        editShareSort.putString(name, value);
        editShareSort.apply();
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            return false;
        }
    }

}
