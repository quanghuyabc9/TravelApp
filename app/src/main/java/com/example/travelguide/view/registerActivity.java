package com.example.travelguide.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.travelguide.R;
import com.example.travelguide.network.UserService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class registerActivity extends AppCompatActivity {
    private EditText edt_name, edt_mail, edt_phone, edt_password, edt_confirmPassword;
    private Button btn_register;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        AnhXa();
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setEmail(edt_mail.getText().toString());
                user.setPhone(edt_phone.getText().toString());
                user.setPassword(edt_password.getText().toString());

                userService = APIClient.getClient().create(UserService.class);
                Call<UserResponse> call = userService.createAccount(user);
                call.enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if(response.isSuccessful()) {
                            Toast.makeText(registerActivity.this,"register Success :)",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(registerActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }
                        else {
                            if(response.code()==400){
                                Toast.makeText(registerActivity.this,"Mail or phone has register:(",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(registerActivity.this,"register not correct:(",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        Toast.makeText(registerActivity.this, " fail:(", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void AnhXa() {
        edt_name = (EditText) findViewById(R.id.input_fullName);
        edt_mail = (EditText) findViewById(R.id.input_email);
        edt_phone = (EditText) findViewById(R.id.input_phone);
        edt_password = (EditText) findViewById(R.id.input_passWord);
        edt_confirmPassword = (EditText) findViewById(R.id.input_confirmPassWord);
        btn_register = (Button) findViewById(R.id.register);
    }
}
