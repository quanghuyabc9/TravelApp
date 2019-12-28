package com.ygaps.travelapp.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ygaps.travelapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.ygaps.travelapp.utils.DateTimeTool.convertMillisToDateTime;

public class StopPointDialogTab1TourDetail extends Fragment {

    private StopPointDialogTab1TourDetailListener listener;

    private StopPointInfo pointInfo;
    private String tourId;
    private boolean isHost;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_stop_point_tour_detail_tab1, container, false);

        //get token from login
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        final String accessToken = sharedPref.getString(getString(R.string.saved_access_token),null);

        Bundle bundle = getArguments();
        String JSONPointInfo = bundle.getString("JSONPointInfo");
        tourId = bundle.getString("TourId");
        isHost = bundle.getBoolean("IsHost");
        pointInfo = new Gson().fromJson(JSONPointInfo, new TypeToken<StopPointInfo>(){}.getType());

        final EditText txtSpName = view.findViewById(R.id.stop_point_name_update);
        //final TextView txtServiceID = view.findViewById(R.id.frame_service_type_info);
        final EditText txtAddress = view.findViewById(R.id.address_update);
        //final TextView txtProvinceID = view.findViewById(R.id.frame_province_type_info);

        final EditText txtMinCost = view.findViewById(R.id.min_cost_update);
        final EditText txtMaxCost = view.findViewById(R.id.max_cost_update);

        final TextView txtArriveDate = view.findViewById(R.id.arrive_date_update);
        final TextView txtArriveTime = view.findViewById(R.id.arrive_time_update);

        final TextView txtLeaveDate = view.findViewById(R.id.leave_date_update);
        final TextView txtLeaveTime = view.findViewById(R.id.leave_time_update);

        if (!isHost){
            txtSpName.setInputType(InputType.TYPE_NULL);
            txtSpName.setFocusable(false);
            txtMaxCost.setInputType(InputType.TYPE_NULL);
            txtMaxCost.setFocusable(false);
            txtMinCost.setInputType(InputType.TYPE_NULL);
            txtMinCost.setFocusable(false);
            txtAddress.setInputType(InputType.TYPE_NULL);
            txtAddress.setFocusable(false);

        }

        txtSpName.setText(pointInfo.getName());
        txtAddress.setText(pointInfo.getAddress());
        txtMinCost.setText(Long.toString(pointInfo.getMinCost()));
        txtMaxCost.setText(Long.toString(pointInfo.getMaxCost()));

        String arriveDateTime = convertMillisToDateTime(pointInfo.getArriveAt());
        String arrArriveDateTime[] = arriveDateTime.split(" ", 2);
        txtArriveDate.setText(arrArriveDateTime[0]);
        txtArriveTime.setText(arrArriveDateTime[1]);

        String leaveDateTime = convertMillisToDateTime(pointInfo.getLeaveAt());
        String arrLeaveDateTime[] = leaveDateTime.split(" ", 2);
        txtLeaveDate.setText(arrLeaveDateTime[0]);
        txtLeaveTime.setText(arrLeaveDateTime[1]);

        String[] province = getResources().getStringArray(R.array.province);
        String[] serviceType = getResources().getStringArray(R.array.serviceName);


        //Spinner service type
        final Spinner spinner1 = view.findViewById(R.id.spinner_service_type_update);
        final TextView textViewSpinner1 = view.findViewById(R.id.text_view_service_type_update);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),R.array.serviceName, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        final RelativeLayout frameServiceType = view.findViewById(R.id.frame_service_type);
        int indexSvId = pointInfo.getServiceTypeId() - 1;
        if (indexSvId >= serviceType.length || indexSvId < 0){
            indexSvId = 0;
        }
        spinner1.setSelection(indexSvId);
        if (!isHost){
            spinner1.setVisibility(View.GONE);
            textViewSpinner1.setVisibility(View.VISIBLE);
            textViewSpinner1.setText(adapter1.getItem(indexSvId).toString());
        }
        else{
            spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        //Spinner province
        final Spinner spinner2 = view.findViewById(R.id.spinner_province_update);
        final TextView textViewSpinner2 = view.findViewById(R.id.text_view_province_update);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),R.array.province, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        int indexProvinceId = pointInfo.getProvinceId() - 1;
        if (indexProvinceId >= province.length || indexProvinceId < 0){
            indexProvinceId = 0;
        }
        spinner2.setSelection(indexProvinceId);
        if (!isHost){
            spinner2.setVisibility(View.GONE);
            textViewSpinner2.setVisibility(View.VISIBLE);
            textViewSpinner2.setText(adapter2.getItem(indexProvinceId).toString());
        }
        else {
            spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        //Set arrive time picker
        ImageButton setTimeBtn =  view.findViewById(R.id.arrive_time_btn_update);
        if (!isHost){
            setTimeBtn.setVisibility(View.GONE);
        }
        else {
            setTimeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            getContext(),
                            AlertDialog.THEME_HOLO_LIGHT,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    String time = hourOfDay + ":" + minute;
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                                    try {
                                        txtArriveTime.setText(dateFormat.format(dateFormat.parse(time)));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                            Calendar.getInstance().get(Calendar.MINUTE),
                            DateFormat.is24HourFormat(getContext())
                    );
                    timePickerDialog.setTitle("");
                    timePickerDialog.show();
                }
            });
        }
        //Set arrive date picker
        ImageButton arriveDateBtn = view.findViewById(R.id.arrive_date_btn_update);
        if (!isHost){
            arriveDateBtn.setVisibility(View.GONE);
        }
        else {
            arriveDateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            getContext(),
                            AlertDialog.THEME_HOLO_LIGHT,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    try {
                                        txtArriveDate.setText(dateFormat.format(dateFormat.parse(date)));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            Calendar.getInstance().get(Calendar.YEAR),
                            Calendar.getInstance().get(Calendar.MONTH),
                            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.show();
                }
            });
        }

        //Set leave date picker
        ImageButton leaveDateBtn = view.findViewById(R.id.leave_date_btn_update);
        if (!isHost){
            leaveDateBtn.setVisibility(View.GONE);
        }
        else {
            leaveDateBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            getContext(),
                            AlertDialog.THEME_HOLO_LIGHT,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                    try {
                                        txtLeaveDate.setText(dateFormat.format(dateFormat.parse(date)));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            Calendar.getInstance().get(Calendar.YEAR),
                            Calendar.getInstance().get(Calendar.MONTH),
                            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.show();
                }
            });
        }


        //Set leave time picker
        ImageButton leaveTimeBtn =  view.findViewById(R.id.leave_time_btn_update);
        if (!isHost){
            leaveTimeBtn.setVisibility(View.GONE);
        }
        else {
            leaveTimeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            getContext(),
                            AlertDialog.THEME_HOLO_LIGHT,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    String time = hourOfDay + ":" + minute;
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                                    try {
                                        txtLeaveTime.setText(dateFormat.format(dateFormat.parse(time)));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                            Calendar.getInstance().get(Calendar.MINUTE),
                            DateFormat.is24HourFormat(getContext())
                    );
                    timePickerDialog.setTitle("");
                    timePickerDialog.show();
                }
            });
        }



        Button updateButton = view.findViewById(R.id.button_update);
        if (!isHost){
            updateButton.setVisibility(View.GONE);
        }
        else {
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String spname = txtSpName.getText().toString();
                    int serviceTypeId = spinner1.getSelectedItemPosition() + 1;
                    String address = txtAddress.getText().toString();
                    int provinceId = spinner2.getSelectedItemPosition() + 1;

                    String strMinCost = txtMinCost.getText().toString();
                    String strMaxCost = txtMaxCost.getText().toString();
                    long minCost, maxCost;
                    if (TextUtils.isEmpty(strMinCost)){
                        minCost = 0;
                    }
                    else {
                        minCost = Long.parseLong(strMinCost);
                    }

                    if (TextUtils.isEmpty(strMaxCost)){
                        maxCost = 0;
                    }
                    else {
                        maxCost = Long.parseLong(strMaxCost);
                    }

                    String strArrTime = txtArriveTime.getText().toString();
                    String strArrDate = txtArriveDate.getText().toString();
                    String strLeaveTime = txtLeaveTime.getText().toString();
                    String strLeaveDate = txtLeaveDate.getText().toString();

                    //Check isEmpty in fields: name, date, address
                    boolean isValid = true;
                    if (TextUtils.isEmpty(spname)){
                        txtSpName.setError("Please enter the name");
                        isValid = false;
                    }
                    if (TextUtils.isEmpty(address)){
                        txtAddress.setError("Please enter address");
                        isValid = false;
                    }
                    if (!isValid){
                        return;
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    //Convert ArriveDate String to millis
                    String stringArriveDate = strArrDate + " " + strArrTime + ":00";
                    Date arriveDate = null;
                    try {
                        arriveDate = sdf.parse(stringArriveDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long millisArriveDate = arriveDate.getTime();

                    //Convert LeaveDate String to millis
                    String stringLeaveDate = strLeaveDate + " " + strLeaveTime + ":00";
                    Date leaveDate = null;
                    try {
                        leaveDate = sdf.parse(stringLeaveDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long millisLeaveDate = leaveDate.getTime();

                    pointInfo.setName(spname);
                    pointInfo.setAddress(address);
                    pointInfo.setArriveAt(millisArriveDate);
                    pointInfo.setLeaveAt(millisLeaveDate);
                    pointInfo.setServiceTypeId(serviceTypeId);
                    pointInfo.setMinCost(minCost);
                    pointInfo.setMaxCost(maxCost);
                    pointInfo.setProvinceId(provinceId);

                    //Queue
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    String url = "http://35.197.153.192:3000/tour/set-stop-points";
                    //Json Body
                    JSONObject jsonBody = new JSONObject();
                    try {

                        jsonBody.put("tourId", tourId);
                        JSONArray listUpdateJson = new JSONArray("["+ new Gson().toJsonTree(pointInfo).getAsJsonObject().toString()+"]");
                        jsonBody.put("stopPoints", listUpdateJson);
                        //Set request
                        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                listener.applyDelete();
                                Toast.makeText(getContext(), "Updated 1 stop points", Toast.LENGTH_LONG).show();

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.d("Err", "Error: " + error.getMessage());
                                Log.e("Err", "Site Info Error: " + error.getMessage());
                                Toast.makeText(getContext(), "Failed to update stop points! Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("Authorization",accessToken);
                                return headers;
                            }
                        };
                        requestQueue.add(req);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        //arriveDateText, arriveTimeText, leaveDateText, leaveTimeText, address

        final Button deleteBtn = view.findViewById(R.id.button_delete_update);
        if (!isHost){
            deleteBtn.setVisibility(View.GONE);
        }
        else {
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Queue
                    RequestQueue requestQueue = Volley.newRequestQueue(getContext());
                    String url = "http://35.197.153.192:3000/tour/set-stop-points";
                    //Json Body
                    JSONObject jsonBody = new JSONObject();
                    try {

                        jsonBody.put("tourId", tourId);
                        JSONArray listDeleteJson = new JSONArray("["+pointInfo.getId()+"]");
                        jsonBody.put("deleteIds", listDeleteJson);
                        //Set request
                        JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                listener.applyDelete();
                                Toast.makeText(getContext(), "Deleted 1 stop points", Toast.LENGTH_LONG).show();

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                VolleyLog.d("Err", "Error: " + error.getMessage());
                                Log.e("Err", "Site Info Error: " + error.getMessage());
                                Toast.makeText(getContext(), "Failed to delete stop points! Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("Authorization",accessToken);
                                return headers;
                            }
                        };
                        requestQueue.add(req);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        //arriveDateText, arriveTimeText, leaveDateText, leaveTimeText, address
        return view;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (StopPointDialogTab1TourDetailListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement FragmentListener");
        }
    }

    public interface StopPointDialogTab1TourDetailListener{
        void applyDelete();
        //void fixedMarker(String spName, int serviceTypeId);

    }
}
