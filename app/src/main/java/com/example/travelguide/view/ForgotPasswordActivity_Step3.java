package com.example.travelguide.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.travelguide.R;
import com.example.travelguide.utils.EditTool;

public class ForgotPasswordActivity_Step3 extends AppCompatActivity {

    Button button_Continue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_step3);
        EditTool.CustomizeActionBar("Forgot Password - Step 3/3", ForgotPasswordActivity_Step3.this);

        button_Continue = findViewById(R.id.button_Continue);
        button_Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity_Step3.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                ForgotPasswordActivity_Step3.this.finish();
            }
        });
    }
}
