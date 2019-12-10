package com.example.travelguide.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.travelguide.R;
import com.example.travelguide.utils.EditTool;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditTool.CustomizeActionBar("List Tour", MainActivity.this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListTourFragment()).commit();

        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;
                if (menuItem.isChecked()) return true;
                switch (menuItem.getItemId()){
                    case R.id.nav_list_tour:
                        EditTool.CustomizeActionBar("List Tour", MainActivity.this);
                        selectedFragment = new ListTourFragment();
                        break;
                    case R.id.nav_history:
                        EditTool.CustomizeActionBar("History", MainActivity.this);
                        selectedFragment = new HistoryFragment();
                        break;
                    case R.id.nav_map:
                        EditTool.CustomizeActionBar("Map", MainActivity.this);
                        selectedFragment = new MapFragment();
                        break;
                    case R.id.nav_noti:
                        EditTool.CustomizeActionBar("Notifications", MainActivity.this);
                        selectedFragment = new NotificationsFragment();
                        break;
                    case R.id.nav_setting:
                        EditTool.CustomizeActionBar("Settings", MainActivity.this);
                        selectedFragment = new SettingsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
