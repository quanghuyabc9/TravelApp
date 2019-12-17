package com.ygaps.travelapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ygaps.travelapp.R;

import java.util.regex.Pattern;

import static com.facebook.FacebookSdk.getApplicationContext;

public class EditTool {
    public static void CustomizeActionBar(String title, AppCompatActivity context) {
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

        Drawable upArrow = context.getResources().getDrawable(R.drawable.back_button);
        Bitmap bitmap = ((BitmapDrawable) upArrow).getBitmap();
        upArrow = new BitmapDrawable(context.getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        abar.setHomeAsUpIndicator(upArrow);
    }

    public static void HideSoftKeyboard(Context context) {
        try {
            // use application level context to avoid unnecessary leaks.
            InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            assert inputManager != null;
            inputManager.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isValidEmail(String email) {
        return Pattern.matches("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$", email);
    }

    public static boolean isValidPassword(String password){
        return password.length() >= 4;
    }
}
