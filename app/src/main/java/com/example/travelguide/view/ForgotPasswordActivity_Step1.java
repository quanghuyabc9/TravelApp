package com.example.travelguide.view;

import android.app.ProgressDialog;
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

public class ForgotPasswordActivity_Step1 extends AppCompatActivity {
    // UI references
    Button button_ViaSms, button_ViaEmail;
    EditText editText_Destination;
    Button button_Next;
    int Via; //0: send to phone number, 1: send to email
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_step1);
        EditTool.CustomizeActionBar("Forgot password - Step 1/3", this);

        button_ViaSms = findViewById(R.id.button_ViaSms);
        button_ViaEmail = findViewById(R.id.button_viaEmail);
        editText_Destination = findViewById(R.id.editText_Destination);
        button_Next = findViewById(R.id.button_Next);
        button_ViaSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_Destination.setHint("Enter your phone number");
                Via = 0;
            }
        });
        button_ViaEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText_Destination.setHint(("Enter your email address"));
                Via = 1;
            }
        });
        button_ViaEmail.performClick();
        button_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type, value;

                value = editText_Destination.getText().toString();
                if(value.isEmpty()) {
                    editText_Destination.setError(getString(R.string.error_field_required));
                }
                else {
                    if(Via == 0) type = "phone";
                    else type="email";
                    final ProgressDialog progressDialog = new ProgressDialog(ForgotPasswordActivity_Step1.this);
                    progressDialog.show();
                    OkHttpClient client = new OkHttpClient();

                    RequestBody requestBody = new FormEncodingBuilder()
                            .add("type", type)
                            .add("value", value)
                            .build();
                    Request request = new Request.Builder()
                            .url(Constants.APIEndpoint + "/user/request-otp-recovery")
                            .addHeader("Content-Type", "application/json")
                            .post(requestBody)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            ForgotPasswordActivity_Step1.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ForgotPasswordActivity_Step1.this, "Submit failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            if(response.code() == 200) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    String userId = jsonObject.getString("userId");
                                    Intent intent = new Intent(ForgotPasswordActivity_Step1.this, ForgotPasswordActivity_Step2.class);
                                    intent.putExtra("userId", userId);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    ForgotPasswordActivity_Step1.this.finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if(response.code() == 404) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    final String message = jsonObject.getString("message");
                                    ForgotPasswordActivity_Step1.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            editText_Destination.setError(message);
                                        }
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                ForgotPasswordActivity_Step1.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ForgotPasswordActivity_Step1.this, "Submit failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }
}
