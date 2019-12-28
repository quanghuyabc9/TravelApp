package com.ygaps.travelapp.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ygaps.travelapp.R;


public class StopPointTourDetailDialog extends AppCompatDialogFragment {

    private ViewPager mViewPager;

    View rootview;

    TabLayout tabLayout;
    ViewPager viewPager;

    StopPointInfo pointInfo;
    String tourId;
    boolean isHost;

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
            tourId = getArguments().getString("TourId");
            isHost = getArguments().getBoolean("IsHost");
            String JSONPointInfo = getArguments().getString("JSONPointInfo");
            pointInfo = new Gson().fromJson(JSONPointInfo, new TypeToken<StopPointInfo>(){}.getType());

            Bundle bundle1 = new Bundle();
            bundle1.putString("JSONPointInfo", JSONPointInfo);
            bundle1.putString("TourId", tourId);
            bundle1.putBoolean("IsHost", isHost);

            Bundle bundle2 = new Bundle();
            bundle2.putInt("serviceId", pointInfo.getServiceId());

            //Set tab navigation
            tabLayout = (TabLayout) rootview.findViewById(R.id.tabs);
            viewPager = (ViewPager) rootview.findViewById(R.id.sp_container_fragment);
            SectionPageAdapter adapter = new SectionPageAdapter(getChildFragmentManager());

            StopPointDialogTab1TourDetail stopPointDialogTab1 = new StopPointDialogTab1TourDetail();
            StopPointDialogTab2 stopPointDialogTab2 = new StopPointDialogTab2();

            //Send data to 2 tabs fragment
            stopPointDialogTab1.setArguments(bundle1);
            stopPointDialogTab2.setArguments(bundle2);

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
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        String contextDialogString = getArguments().getString("ContextDialog");
        TourDetailStoppointFragment contextDialog = new Gson().fromJson(contextDialogString, new TypeToken<TourDetailStoppointFragment>(){}.getType());

//        try {
//            listener = (StopPointTourDetailDialogListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + "must implement DialogListener");
//        }
    }

}
