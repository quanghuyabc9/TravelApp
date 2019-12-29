package com.ygaps.travelapp.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.manager.Constants;
import com.ygaps.travelapp.utils.EditTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class TourDetailActivity extends AppCompatActivity implements StopPointDialogTab1TourDetail.StopPointDialogTab1TourDetailListener {

    private TabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private RelativeLayout mainContainer;
    private ImageButton deleleTourBtn;
    private ImageButton followButton;
    private TextView textView_tourName;
    //data
    private String tourId = null;
    private String tourName = null;
    private String authorization = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourdetail);
        //EditTool.CustomizeActionBar("Tour Detail", this);
        getSupportActionBar().hide();

        tabLayout = findViewById(R.id.tablayout_tourdetail_tab);
        appBarLayout = findViewById(R.id.appbarlayout_tourdetail_appbar);
        viewPager = findViewById(R.id.viewpaper_tourdetail_mainview);
        mainContainer = findViewById(R.id.linearlayout_tourdetail_maincontainer);
        deleleTourBtn = findViewById(R.id.imagebutton_tourdetail_deletetour);
        followButton = findViewById(R.id.imagebutton_tourdetail_followtour);
        textView_tourName = findViewById(R.id.textView_tourDetail_tourName);
        mainContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditTool.HideSoftKeyboard(TourDetailActivity.this);
            }
        });
        //Initialize data
        tourId = getIntent().getStringExtra("TourId");
        tourName = getIntent().getStringExtra("TourName");
        textView_tourName.setText(tourName);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
        authorization = sharedPreferences.getString(getString(R.string.saved_access_token), null);
        deleleTourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(authorization == null || tourId == null)
                    return;
                OkHttpClient client = new OkHttpClient();
                final RequestBody requestBody = new FormEncodingBuilder()
                        .add("id", tourId)
                        .add("status", "-1")
                        .build();
                Request request = new Request.Builder()
                        .url(Constants.APIEndpoint + "/tour/update-tour")
                        .addHeader("Authorization", authorization)
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        final IOException fe = e;
                        TourDetailActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TourDetailActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if(response.code() == 200) {
                            TourDetailActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TourDetailActivity.this, getString(R.string.successful), Toast.LENGTH_SHORT).show();
                                }
                            });
                            Intent intent = new Intent(TourDetailActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            TourDetailActivity.this.finish();
                        }
                        else if(response.code() == 404 || response.code() == 403 || response.code() == 500) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                final String message = jsonObject.getString("message");
                                TourDetailActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TourDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                            catch (JSONException e) {
                                final JSONException fe = e;
                                TourDetailActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TourDetailActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        else {
                            TourDetailActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(TourDetailActivity.this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TourDetailActivity.this, FollowTourActivity.class);
                intent.putExtra("ToudId", tourId);
                startActivity(intent);
            }
        });

        TourDetailViewPageAdapter adapter = new TourDetailViewPageAdapter(getSupportFragmentManager());
        Bundle bundle_tourId = new Bundle();
        bundle_tourId.putString("TourId", tourId);
        bundle_tourId.putString("TourName", tourName);

        TourDetailInfoFragment tourDetailInfoFragment = new TourDetailInfoFragment();
        TourDetailStoppointFragment tourDetailStoppointFragment = new TourDetailStoppointFragment();
        TourDetailMemberFragment tourDetailMemberFragment = new TourDetailMemberFragment();
        TourDetailCommentsFragment tourDetailCommentsFragment = new TourDetailCommentsFragment();

        tourDetailInfoFragment.setArguments(bundle_tourId);
        tourDetailStoppointFragment.setArguments(bundle_tourId);
        tourDetailMemberFragment.setArguments(bundle_tourId);
        tourDetailCommentsFragment.setArguments(bundle_tourId);

        adapter.AddFragment(tourDetailInfoFragment, "General");
        adapter.AddFragment(tourDetailStoppointFragment, "Stop points");
        adapter.AddFragment(tourDetailMemberFragment, "Members");
        adapter.AddFragment(tourDetailCommentsFragment, "Comments");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
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

    @Override
    public void applyDelete() {
        TabLayout.Tab tableLayout1 = tabLayout.getTabAt(1);
        TabLayout.Tab tableLayout2 = tabLayout.getTabAt(2);
        tableLayout2.select();
        tableLayout1.select();
    }
}
