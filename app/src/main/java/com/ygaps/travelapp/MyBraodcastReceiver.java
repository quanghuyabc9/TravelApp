package com.ygaps.travelapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.ResponseBody;
import com.ygaps.travelapp.manager.Constants;
import com.ygaps.travelapp.view.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.ygaps.travelapp.view.CreateStopsActivity.JSON;

public class MyBraodcastReceiver extends BroadcastReceiver {
    private String tourId;
    private String message;
    private String accessToken;
    @Override
    public void onReceive(Context context, Intent intent) {
        //break point kod
        //get token from login
        SharedPreferences sharedPref = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE); //Giờ thử set cứng nha wtf vl nãy h m ko sửa hả
        accessToken = sharedPref.getString("login_access_token", null); //tie lay token thoi //sua r,

        tourId = intent.getStringExtra("tourId");
        message = intent.getStringExtra("message");
        //get token from login
        String action = intent.getStringExtra("action");
        if(action.equals("OK")){
            AcceptInvitation();
        }

        else if(action.equals("Cancel")){
            RefuseInvitation();
        }
        //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it); // dong 34 m a,i biet :v cua m  lay token, m lay o ben kia roi day qua day di//Lay moi cai token day qua nua roi gui la ok ak
    }

    private void RefuseInvitation() {

    }

    private void AcceptInvitation() {
        JSONObject jsonBody = new JSONObject();
        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            jsonBody.put("tourId",tourId);
            jsonBody.put("isAccepted",true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(JSON, jsonBody.toString());//vl crash o day ne
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(Constants.APIEndpoint + "/tour/response/invitation")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", accessToken)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {

            }
            @Override
            public void onResponse(final com.squareup.okhttp.Response response) throws IOException {
                if(response.code() == 200){
                    Log.d("OOKKKKKKKKKKKKKKKKK", "OK");
                }
                else {
                    Log.d("hihi", "No");
                } //bấm ok đi, bam r kỳ ta à đm biết rồi
            }
        });
    }
}
