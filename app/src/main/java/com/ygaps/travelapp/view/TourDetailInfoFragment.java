package com.ygaps.travelapp.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.manager.Constants;
import com.ygaps.travelapp.utils.DateTimeTool;
import com.ygaps.travelapp.utils.EditTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class TourDetailInfoFragment extends Fragment {

    View view;
    // Ui reference
    private  TextView textView_date,
             textView_people,
             textView_cost,
             textView_security;
    ImageButton editButton;

    // Tour detail data
    String authorization = null;
    private String tourId = null,
                   tourName = null;


    public TourDetailInfoFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tourdetail_info, container, false);
        // Initialize tour detail data here
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
        authorization = sharedPreferences.getString(getString(R.string.saved_access_token), null);
        //tourId = "300";
        tourId = getArguments().getString("TourId");
        tourName = getArguments().getString("TourName");
        // Get view
        textView_date = view.findViewById(R.id.textview_tourdetail_editinfo_date);
        textView_people = view.findViewById(R.id.textview_tourdetail_editinfo_people);
        textView_cost = view.findViewById(R.id.textview_tourdetail_editinfo_cost);
        textView_security = view.findViewById(R.id.textview_tourdetail_editinfo_security);
        editButton = view.findViewById(R.id.imagebutton_tourdetail_editinfo_editbtn);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), TourDetailEditInfoActivity.class);
                intent.putExtra("TourId", tourId);
                intent.putExtra("TourName", tourName);
                startActivity(intent);
            }
        });
        getTourInfo();
        return view;
    }

    private void getTourInfo() {
        if (authorization == null || tourId == null)
            return;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.APIEndpoint + "/tour/info" + "?tourId=" + tourId)
                .addHeader("Authorization", authorization)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final IOException fe = e;
                ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(view.getContext(), fe.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().string());
                        //final String tourname_data = jsonObject.getString("name");

                        final long milis_startDate_data = jsonObject.getLong("startDate"),
                                   milis_endDate_data = jsonObject.getLong("endDate");

                        String  startDate_data = DateTimeTool.convertMillisToDateTime(milis_startDate_data),
                                endDate_data = DateTimeTool.convertMillisToDateTime(milis_endDate_data);

                        final String startDate_dataFixed = startDate_data.split(" ")[0];
                        final String endDate_dataFixed = endDate_data.split(" ")[0];

                        final String adults_data = jsonObject.getString("adults"),
                                     children_data = jsonObject.getString("childs"),
                                     minCost_data = jsonObject.getString("minCost"),
                                     maxCost_data = jsonObject.getString("maxCost");
                        final String isPrivate_data = jsonObject.getString("isPrivate");

                        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final String date = startDate_dataFixed + " - " + endDate_dataFixed;
                                final String people = adults_data + " adults" + " - " + children_data + " childs";
                                final String cost = minCost_data + " - " + maxCost_data;
                                String security = "Public";
                                if (isPrivate_data.equals("true"))
                                    security = "Private";
                                textView_date.setText(date);
                                textView_people.setText(people);
                                textView_cost.setText(cost);
                                textView_security.setText(security);
                            }
                        });
                    } catch (JSONException e) {
                        final JSONException fe = e;
                        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(), fe.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else if (response.code() == 404 || response.code() == 500) {
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().toString());
                        final String message = jsonObject.getString("message");
                        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                        final JSONException fe = e;
                        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(), fe.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}