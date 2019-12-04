package com.example.travelguide.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.travelguide.R;
import com.example.travelguide.manager.Constants;
import com.facebook.login.LoginManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingsFragment extends Fragment {

    private Button signOutButton;
    private Spinner spinner_SettingLanguge;
    private TextView textView_UserName;
    private Button button_EditProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        textView_UserName = view.findViewById(R.id.textView_UserName);
        signOutButton = view.findViewById(R.id.signOutButton);
        button_EditProfile = view.findViewById(R.id.button_EditProfile);

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out facebook
                LoginManager.getInstance().logOut();

                SharedPreferences sharedPref = v.getContext().getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_access_token),"");
                editor.apply();

                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                ((Activity)v.getContext()).finish();
            }
        });

        button_EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        spinner_SettingLanguge = view.findViewById(R.id.spinner_SettingLanguage);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(),
                android.R.layout.simple_spinner_item);
        arrayAdapter.add("English");
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_SettingLanguge.setAdapter(arrayAdapter);

        SharedPreferences sharedPref = view.getContext().getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        String accessToken = sharedPref.getString(getString(R.string.saved_access_token), null);
        if(accessToken!= null) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.APIEndpoint + "/user/info")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Authorization", accessToken)
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //Log.i("Error",e.getMessage());
                    ((Activity)view.getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), getString(R.string.error_check_network_connection), Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Toast.makeText(view.getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        final String fullName = jsonObject.getString("fullName");
                        ((Activity)view.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView_UserName.setText(fullName);
                            }
                        });
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(view.getContext(), "Fail to load user information", Toast.LENGTH_SHORT).show();
                    }
                }
            });
       }
        return view;
    }


}

