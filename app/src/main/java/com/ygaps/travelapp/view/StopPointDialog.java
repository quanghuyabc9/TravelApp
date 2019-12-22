package com.ygaps.travelapp.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.ygaps.travelapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import static android.widget.ArrayAdapter.createFromResource;
import static com.ygaps.travelapp.utils.DateTimeTool.convertMillisToDateTime;


public class StopPointDialog extends AppCompatDialogFragment{

    private ViewPager mViewPager;

    View rootview;

    TabLayout tabLayout;
    ViewPager viewPager;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.dialog_stop_point, container,false);
        if (getDialog() != null && getDialog().getWindow() != null) {

            //Set transparent background
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

            //Get data from root activity, add to bundle
            String JSONPointInfo = getArguments().getString("JSONPointInfo");
            Bundle bundle = new Bundle();
            bundle.putString("JSONPointInfo", JSONPointInfo);

            //Set tab navigation
            tabLayout = (TabLayout) rootview.findViewById(R.id.tabs);
            viewPager = (ViewPager) rootview.findViewById(R.id.sp_container_fragment);
            SectionPageAdapter adapter = new SectionPageAdapter(getChildFragmentManager());

            StopPointDialogTab1 stopPointDialogTab1 = new StopPointDialogTab1();
            StopPointDialogTab2 stopPointDialogTab2 = new StopPointDialogTab2();

            //Send data to 2 tabs fragment
            stopPointDialogTab1.setArguments(bundle);
            stopPointDialogTab2.setArguments(bundle);

            //Add tab fragment to adapter
            adapter.addFragment(stopPointDialogTab1, "GENERAL");
            adapter.addFragment(stopPointDialogTab2, "REVIEWS");
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);

            //Set event click cancelBtn
            ImageButton cancelBtn = rootview.findViewById(R.id.cancel_stop_point_infor);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        //
        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
