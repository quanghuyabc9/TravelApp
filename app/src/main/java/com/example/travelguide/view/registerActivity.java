package com.example.travelguide.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
        getSupportActionBar().setTitle("Sign up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        AnhXa();
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cancel = isCheckInformation();
                if (cancel == true) {

                } else {
                    User user = new User();
                    user.setEmail(edt_mail.getText().toString());
                    user.setPhone(edt_phone.getText().toString());
                    user.setPassword(edt_password.getText().toString());

                    userService = APIClient.getClient().create(UserService.class);
                    Call<UserResponse> call = userService.createAccount(user);
                    call.enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(registerActivity.this, "register Success :)", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(registerActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else {
                                if (response.code() == 400) {
                                    Toast.makeText(registerActivity.this, "Mail or phone has register:(", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(registerActivity.this, "register not correct:(", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            Toast.makeText(registerActivity.this, " fail:(", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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

    private boolean isCheckInformation(){
        boolean temp=false;
        edt_password.setError(null);
        edt_mail.setError(null);
        edt_phone.setError(null);

        String emailView=edt_mail.getText().toString();
        String passwordView=edt_password.getText().toString();
        String passwordconView=edt_confirmPassword.getText().toString();
        String phoneView=edt_phone.getText().toString();

        //Check mail
        if(TextUtils.isEmpty(emailView)){
            edt_mail.setError(getString(R.string.error_invalid_email));
            temp=true;
        }

        //Check password
        if(TextUtils.isEmpty(passwordView)){
            edt_password.setError(getString(R.string.error_invalid_password));
            temp = true;
        }

        //Check phone
        if(TextUtils.isEmpty(phoneView)){
            edt_phone.setError(getString(R.string.error_field_required));
            temp=true;
        }else if(isPhoneValue(phoneView)){
            edt_phone.setError(getString(R.string.error_invalid_phone));
            temp=true;
        }

        //Check password with confirmPassword
        if(passwordView.equals(passwordconView)==false && passwordView.contentEquals(passwordconView)==false){
            edt_confirmPassword.setError(getString(R.string.error_invalid_confirmPassword));
            temp = true;
        }
        return temp;
    }
    //Mật khẩu phải từ 4 kí tự trở lên
    private boolean isPasswordValue(String pass){
        int lenPass=pass.length();
        if(lenPass>=4) {
            return false;
        }
        else{
            return true;
        }
    }

    //So số điện thoại phải từ 6 đến 12 kí tự
    private boolean isPhoneValue(String phone){
        int lenPhone=phone.length();

        if(lenPhone>=6 && lenPhone<=12)
            return false;
        return true;
    }
}
