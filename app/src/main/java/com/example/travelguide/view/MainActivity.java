package com.example.travelguide.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.travelguide.R;
import com.example.travelguide.manager.MyApplication;
import com.example.travelguide.network.MyAPIClient;
import com.example.travelguide.network.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        MyApplication app = (MyApplication) getApplication();
        TextView tv = (TextView) findViewById(R.id.textView);
        //tv.setText(getString(R.string.hello) + " " + app.getTokenInfo().getUserName());

        userService = MyAPIClient.getInstance().getAdapter().create(UserService.class);
        Button logout = (Button) findViewById(R.id.b_Logout);
//            @Override
//            public void onClick(View view) {
//                mProgressDialog.show();
//                Call<Void> call = userService.logout();
//                call.enqueue(new Callback<Void>() {
//                    @Override
//                    public void onResponse(Call<Void> call, Response<Void> response) {
//                        // Clear token
//                        MyAPIClient.getInstance().setAccessToken(null);
//                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
//                        SharedPreferences.Editor editor = sharedPref.edit();
//                        editor.remove(MainActivity.this.getString(R.string.saved_access_token));
//                        editor.remove(MainActivity.this.getString(R.string.saved_access_token_time));
//                        editor.commit();
//                        // Open LoginActivity
//                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
//                        mProgressDialog.hide();
//                        MainActivity.this.startActivity(intent);
//                        MainActivity.this.finish();
//                    }
//
//                    @Override
//                    public void onFailure(Call<Void> call, Throwable t) {
//                        mProgressDialog.hide();
//                    }
//                });
//            }
//        });

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if(mProgressDialog != null)
//            mProgressDialog.dismiss();
//    }
//
    }
}
