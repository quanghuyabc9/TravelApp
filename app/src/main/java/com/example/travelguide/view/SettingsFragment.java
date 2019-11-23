package com.example.travelguide.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.travelguide.R;
import com.facebook.login.LoginManager;

public class SettingsFragment extends Fragment {

    private Button signOutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        signOutButton = view.findViewById(R.id.signOutButton);

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

        return view;
    }
}
