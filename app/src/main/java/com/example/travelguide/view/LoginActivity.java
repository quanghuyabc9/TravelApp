package com.example.travelguide.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelguide.R;
import com.example.travelguide.manager.MyApplication;
import com.example.travelguide.model.LoginRequest;
import com.example.travelguide.model.LoginResponse;
import com.example.travelguide.network.MyAPIClient;
import com.example.travelguide.network.UserService;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public static String TAG  = "LoginActivity";

    // UI references.
    private AutoCompleteTextView emailPhoneView;
    private EditText passwordView;
    private UserService userService;
    private RelativeLayout relLayout_SignInFormWithAppName, relLayout_SignUpForgotPwBtn;

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            relLayout_SignInFormWithAppName.setVisibility(View.VISIBLE);
            relLayout_SignUpForgotPwBtn.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        relLayout_SignInFormWithAppName = findViewById(R.id.relLayout_SignInFromWithAppName);
        relLayout_SignUpForgotPwBtn = findViewById(R.id.relLayout_SignInForgotPwBtn);

        handler.postDelayed(runnable, 2000);

        userService = MyAPIClient.getInstance().getAdapter().create(UserService.class);

        // Set up the login form.
        emailPhoneView = findViewById(R.id.emailPhone);
        passwordView = findViewById(R.id.password);

        Button mEmailSignInButton = findViewById(R.id.signInButton);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
}

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        emailPhoneView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = emailPhoneView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
//        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            //focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            emailPhoneView.setError(getString(R.string.error_field_required));
            //focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailPhoneView.setError(getString(R.string.error_invalid_email));
            //focusView = mEmailView;
            cancel = true;
        }

        if(cancel == true){

        }else{
            final LoginRequest request = new LoginRequest();
            request.setUsername(email);
            request.setPassword(password);
            Call<LoginResponse> call = userService.login(request);

            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    //Login successfully
                    if(response.code() == 200) {
                        // Save login info
                        MyAPIClient.getInstance().setAccessToken(response.body().getToken());
                        long time = (new Date()).getTime() / 1000;
                        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.saved_access_token), response.body().getToken());
                        editor.putLong(getString(R.string.saved_access_token_time), time);
                        editor.commit();

                        MyApplication app = (MyApplication) LoginActivity.this.getApplication();
                        app.setTokenInfo((response.body()));

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        LoginActivity.this.finish();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.d(TAG, t.getMessage());
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
//        return email.contains("@");
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

}
