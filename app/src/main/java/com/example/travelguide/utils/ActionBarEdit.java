package com.example.travelguide.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.travelguide.R;

public class ActionBarEdit extends AppCompatActivity {
    public static void Customize(String title, AppCompatActivity context) {
        //Customize the ActionBar
        final ActionBar abar = context.getSupportActionBar();
        View viewActionBar = context.getLayoutInflater().inflate(R.layout.abs_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        viewActionBar.setBackgroundColor(ContextCompat.getColor(context,R.color.window_title_background));
        textviewTitle.setText(title);
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
//        abar.setDisplayHomeAsUpEnabled(true);
//        abar.setIcon(R.color.white);
//        abar.setHomeButtonEnabled(false);
    }
}
