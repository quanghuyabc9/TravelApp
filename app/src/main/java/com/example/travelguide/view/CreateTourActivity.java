package com.example.travelguide.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.travelguide.R;
import com.example.travelguide.utils.EditTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateTourActivity extends AppCompatActivity {

    private DatePickerDialog.OnDateSetListener mDateSetListener1;
    private DatePickerDialog.OnDateSetListener mDateSetListener2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tour);

        EditTool.CustomizeActionBar("Create Tour", this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            throw new AssertionError();
        }
        actionBar.setDisplayHomeAsUpEnabled(true);

        //get token from login
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        final String accessToken = sharedPref.getString(getString(R.string.saved_access_token),null);

        //declare views
        final EditText editTextName = findViewById(R.id.create_tour_name);
        final TextView editStartDate = findViewById(R.id.create_tour_start_date);
        final TextView editEndDate = findViewById(R.id.create_tour_end_date);
        final EditText editAdults = findViewById(R.id.create_tour_adults);
        final EditText editChildren = findViewById(R.id.create_tour_children);
        final EditText editMinCost = findViewById(R.id.create_tour_min_cost);
        final EditText editMaxCost = findViewById(R.id.create_tour_max_cost);
        final CheckBox checkBoxIsPrivate = findViewById(R.id.is_private_radio);


        //set calendar view choosing date
        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateTourActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener1,
                        year,month,day);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateTourActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener2,
                        year,month,day);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month +1;
                String date = dayOfMonth + "/" + month + "/" + year;
                editStartDate.setText(date);
            }
        };
        mDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month +1;
                String date = dayOfMonth + "/" + month + "/" + year;
                editEndDate.setText(date);
            }
        };

        // handle when submit button clicked
        final Button submitBtn = findViewById(R.id.create_tour_submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String name = editTextName.getText().toString();
                String startDate = editStartDate.getText().toString();
                String endDate = editEndDate.getText().toString();
                boolean isPrivate = checkBoxIsPrivate.isChecked();
                String adults = editAdults.getText().toString();
                String childs = editChildren.getText().toString();
                String minCost = editMinCost.getText().toString();
                String maxCost = editMaxCost.getText().toString();

                long numAduts;
                long numChilds;
                long numMinCost;
                long numMaxCost;

                long millisStartDate = 0;
                long millisEndDate = 0;

                //Check isEmpty in fields: name, date
                if (TextUtils.isEmpty(name)){
                    editStartDate.requestFocus();
                    editTextName.setError("Please enter the name");
                    return;
                }
                if (TextUtils.isEmpty(startDate)){
                    editStartDate.requestFocus();
                    editStartDate.setError("Please enter start date");
                    return;
                }
                if (TextUtils.isEmpty(endDate)){
                    editStartDate.requestFocus();
                    editEndDate.setError("Please enter end date");
                    return;
                }

                if (TextUtils.isEmpty(adults)){
                    numAduts = 0;
                }
                else{
                    numAduts = Long.parseLong(adults);
                }
                if (TextUtils.isEmpty(childs)){
                    numChilds = 0;
                }

                else{
                    numChilds = Long.parseLong(childs);
                }
                if (TextUtils.isEmpty(minCost)){
                    numMinCost = 0;
                }
                else{
                    numMinCost = Long.parseLong(minCost);
                }

                if (TextUtils.isEmpty(maxCost)){
                    numMaxCost = 0;
                }
                else{
                    numMaxCost = Long.parseLong(maxCost);
                }

                //Convert date to millis second
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date dateStarDate = format.parse(startDate);
                    Date dateEndDate = format.parse(endDate);

                    if (dateStarDate == null) throw new AssertionError();
                    millisStartDate = dateStarDate.getTime();
                    if (dateEndDate == null) throw new AssertionError();
                    millisEndDate = dateEndDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.d("millisStartDate", Long.toString(millisStartDate));
                Log.d("millisStartDate", Long.toString(millisEndDate));

                //Send request create tour to server
                RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());
                String url="http://35.197.153.192:3000/tour/create";
                //Create request's body
                JSONObject jsonBody = new JSONObject();
                try {

                    jsonBody.put("name", name);
                    jsonBody.put("startDate", millisStartDate);
                    jsonBody.put("endDate", millisEndDate);
                    jsonBody.put("isPrivate", isPrivate);
                    jsonBody.put("adults", numAduts);
                    jsonBody.put("childs", numChilds);
                    jsonBody.put("minCost", numMaxCost);
                    jsonBody.put("maxCost", numMinCost);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Set request
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Res: ", response.toString());
                        Intent intent = new Intent(CreateTourActivity.this, CreateStops.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Err", "Error: " + error.getMessage());
                        Log.e("Err", "Site Info Error: " + error.getMessage());
                        Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization",accessToken);
                        return headers;
                    }
                };
                //Add request to Queue
                requestQueue.add(req);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return true;
    }
}
