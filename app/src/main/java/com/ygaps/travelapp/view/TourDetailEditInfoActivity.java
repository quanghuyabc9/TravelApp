package com.ygaps.travelapp.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

public class TourDetailEditInfoActivity extends AppCompatActivity {

    // Ui reference
    private EditText tourname;
    private TextView startdate, enddate;
    private EditText adults, children, mincost, maxcost;
    private CheckBox isPrivate;
    private Button update;
    // Tour detail data
    private String authorization = null;
    private String tourId = null,
                   tourName = null;

    private boolean isPrivate_data = false;

    private DatePickerDialog.OnDateSetListener onDataSetListener1;
    private DatePickerDialog.OnDateSetListener onDataSetListener2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourdetail_editinfo);
        EditTool.CustomizeActionBar("Edit Tour", this);
        // Initialize tour detail data here
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
        authorization = sharedPreferences.getString(getString(R.string.saved_access_token), null);
        tourId = getIntent().getStringExtra("TourId");
        tourName = getIntent().getStringExtra("TourName");
        // Get view
        tourname = findViewById(R.id.editext_tourdetail_tourname);
        startdate = findViewById(R.id.textview_tourdetail_startdate);
        enddate = findViewById(R.id.textview_tourdetail_enddate);
        adults = findViewById(R.id.edittext_tourdetail_adults);
        children = findViewById(R.id.edittext_tourdetail_children);
        mincost = findViewById(R.id.edittext_tourdetail_mincost);
        maxcost = findViewById(R.id.edittext_tourdetail_maxcost);
        isPrivate = findViewById(R.id.checkbox_tourdetail_isprivate);
        update = findViewById(R.id.button_tourdetail_update);
        //Specify action
        tourname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    EditTool.HideSoftKeyboard(TourDetailEditInfoActivity.this);
                    handled = true;
                }
                return handled;
            }
        });
        adults.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    children.requestFocus();
                    handled = true;
                }
                return handled;
            }
        });
        children.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mincost.requestFocus();
                    handled = true;
                }
                return handled;
            }
        });
        mincost.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    maxcost.requestFocus();
                    handled = true;
                }
                return handled;
            }
        });
        maxcost.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    EditTool.HideSoftKeyboard(TourDetailEditInfoActivity.this);
                    handled = true;
                }
                return handled;
            }
        });
        startdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH),
                        month = cal.get(Calendar.MONTH),
                        year = cal.get(Calendar.YEAR);
                String[] strDobSplit = startdate.getText().toString().split("/");
                if(strDobSplit.length >= 3) {
                    dayOfMonth = Integer.parseInt(strDobSplit[0]);
                    month = Integer.parseInt(strDobSplit[1]) - 1;
                    year = Integer.parseInt(strDobSplit[2]);
                }
                DatePickerDialog dialog = new DatePickerDialog
                        (v.getContext(),
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                onDataSetListener1,
                                year, month, dayOfMonth);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        onDataSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int month_tmp = month + 1;
                String date = dayOfMonth + "/" + month_tmp + "/" + year;
                startdate.setText(date);
            }
        };
        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH),
                        month = cal.get(Calendar.MONTH),
                        year = cal.get(Calendar.YEAR);
                String[] strDobSplit = enddate.getText().toString().split("/");
                if(strDobSplit.length >= 3) {
                    dayOfMonth = Integer.parseInt(strDobSplit[0]);
                    month = Integer.parseInt(strDobSplit[1]) - 1;
                    year = Integer.parseInt(strDobSplit[2]);
                }
                DatePickerDialog dialog = new DatePickerDialog
                        (v.getContext(),
                                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                                onDataSetListener2,
                                year, month, dayOfMonth);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        onDataSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                int month_tmp = month + 1;
                String date = dayOfMonth + "/" + month_tmp + "/" + year;
                enddate.setText(date);
            }
        };
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTourInfo();
            }
        });
        getTourInfo();
    }
    private void getTourInfo() {
        if(authorization == null || tourId == null)
            return;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.APIEndpoint + "/tour/info" + "?tourId="+tourId)
                .addHeader("Authorization", authorization)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final IOException fe = e;
                TourDetailEditInfoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TourDetailEditInfoActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.code() == 200) {
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().string());
                        final String tourname_data = jsonObject.getString("name");

                        final long milis_startdate_data = jsonObject.getLong("startDate"),
                                milis_enddate_data = jsonObject.getLong("endDate");

                        String startdate_data = DateTimeTool.convertMillisToDateTime(milis_startdate_data),
                                enddate_data = DateTimeTool.convertMillisToDateTime(milis_enddate_data);

                        final String startdate_data_fixed = startdate_data.split(" ")[0];
                        final String enddate_data_fixed = enddate_data.split(" ")[0];

                        final String adults_data = jsonObject.getString("adults"),
                                children_data = jsonObject.getString("childs"),
                                mincost_data = jsonObject.getString("minCost"),
                                maxcost_data = jsonObject.getString("maxCost");
                        isPrivate_data = jsonObject.getBoolean("isPrivate");

                        TourDetailEditInfoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tourname.setText(tourname_data);
                                startdate.setText(startdate_data_fixed);
                                enddate.setText(enddate_data_fixed);
                                adults.setText(adults_data);
                                children.setText(children_data);
                                mincost.setText(mincost_data);
                                maxcost.setText(maxcost_data);
                                isPrivate.setChecked(isPrivate_data);
                            }
                        });
                    } catch (JSONException e) {
                        final JSONException fe = e;
                        TourDetailEditInfoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TourDetailEditInfoActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else if(response.code() == 404 || response.code() == 500) {
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().toString());
                        final String message = jsonObject.getString("message");
                        TourDetailEditInfoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TourDetailEditInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    catch (JSONException e) {
                        final JSONException fe = e;
                        TourDetailEditInfoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TourDetailEditInfoActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else {
                    TourDetailEditInfoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TourDetailEditInfoActivity.this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    private void updateTourInfo() {
        String tourname_data = tourname.getText().toString();
        String startDate_data = startdate.getText().toString();
        String endDate_data = enddate.getText().toString();
        long millisStartDate_data = 0;
        long millisEndDate_data = 0;
        //Convert date to millis second
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dateStarDate = format.parse(startDate_data);
            Date dateEndDate = format.parse(endDate_data);

            if (dateStarDate == null) throw new AssertionError();
            millisStartDate_data = dateStarDate.getTime();
            if (dateEndDate == null) throw new AssertionError();
            millisEndDate_data = dateEndDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String  adults_data = adults.getText().toString(),
                children_data = children.getText().toString(),
                mincost_data = mincost.getText().toString(),
                maxcost_data = maxcost.getText().toString();
        OkHttpClient client = new OkHttpClient();
        final RequestBody requestBody = new FormEncodingBuilder()
                .add("id", tourId)
                .add("name", tourname_data)
                .add("startDate", Long.toString(millisStartDate_data))
                .add("endDate", Long.toString(millisEndDate_data))
                .add("adults", adults_data)
                .add("childs", children_data)
                .add("minCost", mincost_data)
                .add("maxCost", maxcost_data)
                .add("isPrivate",isPrivate_data?"true": "false")
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
                TourDetailEditInfoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TourDetailEditInfoActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if(response.code() == 200) {
                    TourDetailEditInfoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TourDetailEditInfoActivity.this, getString(R.string.successful), Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent = new Intent(TourDetailEditInfoActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    TourDetailEditInfoActivity.this.finish();
                }
                else if(response.code() == 404 || response.code() == 403 || response.code() == 500) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        final String message = jsonObject.getString("message");
                        TourDetailEditInfoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TourDetailEditInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    catch (JSONException e) {
                        final JSONException fe = e;
                        TourDetailEditInfoActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(TourDetailEditInfoActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else {
                    TourDetailEditInfoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TourDetailEditInfoActivity.this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(TourDetailEditInfoActivity.this, TourDetailActivity.class);
        intent.putExtra("TourId", tourId);
        intent.putExtra("TourName", tourName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        TourDetailEditInfoActivity.this.finish();
        return true;
    }
}
