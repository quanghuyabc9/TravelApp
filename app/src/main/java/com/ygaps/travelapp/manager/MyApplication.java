package com.ygaps.travelapp.manager;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;

import com.ygaps.travelapp.R;
import com.ygaps.travelapp.model.LoginResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class MyApplication extends Application {
    private LoginResponse tokenInfo; //use LoginResponse as tokenInfo

    public static String deviceId = null;

    public LoginResponse getTokenInfo() {
        return tokenInfo;
    }

    public static String token = null;

    public static String getToken() {
        return token;
    }

    public void setTokenInfo(LoginResponse tokenInfo) {
        this.tokenInfo = tokenInfo;
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        String str = new Gson().toJson(tokenInfo);
        editor.putString(getString(R.string.token_info_key), str);
        editor.commit();
    }

    public void loadTokenInfo() {
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        String tokenStr = sharedPref.getString(getString(R.string.token_info_key), null);
        if(TextUtils.isEmpty(tokenStr)) {

        }else {
            tokenInfo = new Gson().fromJson(tokenStr, new TypeToken<LoginResponse>() {}.getType());
            token = tokenInfo.getToken();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        loadTokenInfo();
        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

}

