package com.ygaps.travelapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ygaps.travelapp.R;

public class invite_tour extends AppCompatActivity {
    private Button inviteMember,chatMember;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_tour);
        inviteMember=findViewById(R.id.btn_invite);
        chatMember=findViewById(R.id.btn_chat);

        inviteMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(invite_tour.this,InviteMember.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
}
