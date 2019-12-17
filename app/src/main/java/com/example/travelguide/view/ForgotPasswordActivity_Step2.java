package com.example.travelguide.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelguide.R;
import com.example.travelguide.manager.Constants;
import com.example.travelguide.utils.EditTool;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ForgotPasswordActivity_Step2 extends AppCompatActivity {

    //Ui reference
    EditText editText_VerifyCode;
    EditText editText_NewPassword;
    Button button_Next;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_step2);
        EditTool.CustomizeActionBar("Verification - Step 2/3", ForgotPasswordActivity_Step2.this);
        final String userId = getIntent().getStringExtra("userId");
        editText_VerifyCode = findViewById(R.id.editText_VerifyCode);
        editText_NewPassword = findViewById(R.id.editText_NewPassword);
        button_Next = findViewById(R.id.button_Next);

        button_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verifyCode = editText_VerifyCode.getText().toString();
                if(!isValidVerifyCode(verifyCode)) {
                    editText_VerifyCode.setError(getString(R.string.error_invalid_verify_code));
                }
                else {
                    String newPassword = editText_NewPassword.getText().toString();
                    if(isValidPassword(newPassword)) {
                        OkHttpClient client = new OkHttpClient();
                        RequestBody requestBody = new FormEncodingBuilder()
                                .add("userId", userId)
                                .add("newPassword", newPassword)
                                .add("verifyCode", verifyCode)
                                .build();

                        Request request = new Request.Builder()
                                .url(Constants.APIEndpoint + "/user/verify-otp-recovery")
                                .addHeader("Content-Type", "application/json")
                                .post(requestBody)
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                ForgotPasswordActivity_Step2.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ForgotPasswordActivity_Step2.this, "Update password failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                if(response.code() == 200) {
                                    Intent intent = new Intent(ForgotPasswordActivity_Step2.this,ForgotPasswordActivity_Step3.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    ForgotPasswordActivity_Step2.this.finish();
                                }else if(response.code() == 403) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                        final String message = jsonObject.getString("message");
                                        ForgotPasswordActivity_Step2.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                editText_VerifyCode.setError(message);
                                            }
                                        });
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    ForgotPasswordActivity_Step2.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ForgotPasswordActivity_Step2.this, "Update password error", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });

                    }
                }
            }
        });
    }

    private boolean isValidVerifyCode(String verifyCode) {
        return verifyCode.length() == 6;
    }

    private boolean isValidPassword(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}
