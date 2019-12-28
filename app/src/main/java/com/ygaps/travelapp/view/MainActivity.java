package com.ygaps.travelapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.manager.Constants;
import com.ygaps.travelapp.network.MyAPIClient;
import com.ygaps.travelapp.utils.EditTool;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ygaps.travelapp.utils.MyFirebaseService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.ygaps.travelapp.view.CreateStopsActivity.JSON;

public class MainActivity extends AppCompatActivity {

    private String accessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) { //m import di
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditTool.CustomizeActionBar("List Tour", MainActivity.this);
//        MyFirebaseService myFirebaseService = new MyFirebaseService();
//        myFirebaseService.sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken());
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.v("hihi", "getInstanceId failed", task.getException());
                    return;
                }
                // Get new Instance ID token //doi ty
                String fmcToken = task.getResult().getToken();
                sendRegistrationToServer(fmcToken);
            }
        });

        //get token from login
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        accessToken = sharedPref.getString(getString(R.string.saved_access_token), null);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListTourFragment()).commit();
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;
                if (menuItem.isChecked()) return true;
                switch (menuItem.getItemId()){
                    case R.id.nav_list_tour:
                        EditTool.CustomizeActionBar("List Tour", MainActivity.this);
                        selectedFragment = new ListTourFragment();
                        break;
                    case R.id.nav_history:
                        EditTool.CustomizeActionBar("History", MainActivity.this);
                        selectedFragment = new HistoryFragment();
                        break;
                    case R.id.nav_map:
                        selectedFragment = new ExploreFragment();
                        break;
                    case R.id.nav_noti:
                        EditTool.CustomizeActionBar("Notifications", MainActivity.this);
                        selectedFragment = new NotificationsFragment();
                        break;
                    case R.id.nav_setting:
                        EditTool.CustomizeActionBar("Settings", MainActivity.this);
                        selectedFragment = new SettingsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void sendRegistrationToServer(String fcmToken) {
        // TODO: Implement this method to send token to your app server.
//        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
//        Call<ResponseBody> call = MyAPIClient //m gửi như lấy api bình thường á, m ,l àm khác t vl :v ko sao như nhau thôi à. t laấy sẵn cho m cái fcmtoken r, giờ chỉ việc gửi lên như api đặc tả thoi
//                .getInstance() //dung api nao qua fb t gửi link
//                .getUserService() //m viet hàm regeistfirebase đi để gom lại gửi api á, ko phải t
//                .registerFirebase(accessToken,fcmToken, android_id,1 , "1.0"); //chua co token a, ê, token đầu tiên là token tài khoản m đó //gio t lay dung ko lay di
//        call.enqueue(new Callback<ResponseBody>() { //m import, tko biet chon
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                if (response.isSuccessful()) {
//                    Toast.makeText(MainActivity.this, "Ready for notification", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(MainActivity.this, "Not ready for notification", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//            }
//        });
        JSONObject jsonBody = new JSONObject();
        OkHttpClient okHttpClient = new OkHttpClient();
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            jsonBody.put("fcmToken", fcmToken);
            jsonBody.put("deviceId",android_id ); //lay id device di //lay sao vc nay vua no ĩong
            jsonBody.put("platform", 1); //ê cái này số nè
            jsonBody.put("appVersion", "1.0");
            //Log.d("jsonBody", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, jsonBody.toString());
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(Constants.APIEndpoint + "/user/notification/put-token")
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
                    final String jsonResBody = response.body().string();
                    runOnUiThread(new Runnable()  {
                        @Override
                        public void run() {
                            JSONObject responseJSON = null;
                            try {
                                responseJSON = new JSONObject(jsonResBody);
                                Log.d("REEEEEEEEEEEEEEEEEEADY", "Ready for notification aaaaaaaaaaaaaaa");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else {
                    Log.d("FAILEDDDD", "Failllllllllllllllllllllllllllllllllled");
                }
            }
        });
    }
}
