package com.ygaps.travelapp.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.utils.EditTool;

public class TourDetailActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourdetail);
        EditTool.CustomizeActionBar("Tour Detail", this);
        tabLayout = findViewById(R.id.tablayout_tourdetail_tab);
        appBarLayout = findViewById(R.id.appbarlayout_tourdetail_appbar);
        viewPager = findViewById(R.id.viewpaper_tourdetail_mainview);

        TourDetailViewPageAdapter adapter = new TourDetailViewPageAdapter(getSupportFragmentManager());
        adapter.AddFragment(new TourDetailInfoFragment(), "Info");
        adapter.AddFragment(new TourDetailStoppointFragment(), "Stop points");
        adapter.AddFragment(new TourDetailMemberFragment(), "Member");
        adapter.AddFragment(new TourDetailCommentsFragment(), "Comments");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
