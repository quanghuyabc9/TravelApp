package com.example.travelguide.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.service.autofill.OnClickAction;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.travelguide.R;
import com.example.travelguide.utils.ActionBarEdit;
import com.example.travelguide.view.RecyclerDataAdapter;
import com.example.travelguide.view.TourItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBarEdit.Customize("List Tour", MainActivity.this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListTourFragment()).commit();

        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;
                switch (menuItem.getItemId()){
                    case R.id.nav_list_tour:
                        ActionBarEdit.Customize("List Tour", MainActivity.this);
                        selectedFragment = new ListTourFragment();
                        break;
                    case R.id.nav_history:
                        ActionBarEdit.Customize("History", MainActivity.this);
                        selectedFragment = new HistoryFragment();
                        break;
                    case R.id.nav_map:
                        ActionBarEdit.Customize("Map", MainActivity.this);
                        selectedFragment = new MapFragment();
                        break;
                    case R.id.nav_noti:
                        ActionBarEdit.Customize("Notifications", MainActivity.this);
                        selectedFragment = new NotificationsFragment();
                        break;
                    case R.id.nav_setting:
                        ActionBarEdit.Customize("Settings", MainActivity.this);
                        selectedFragment = new SettingsFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });


    }

}
