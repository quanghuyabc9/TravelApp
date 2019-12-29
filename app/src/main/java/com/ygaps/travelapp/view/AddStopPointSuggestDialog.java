package com.ygaps.travelapp.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ygaps.travelapp.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddStopPointSuggestDialog extends AppCompatDialogFragment {
    private AddStopPointSuggestDialog.AddStopPointDialogSuggestListener listener;

    private StopPointInfo suggestedPointInfo;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_add_stop_point_suggested_layout, null);

        String JsonDataString = getArguments().getString("JSONPointInfo");
        suggestedPointInfo = new Gson().fromJson(JsonDataString, new TypeToken<StopPointInfo>(){}.getType());

        final TextView txtSpName = view.findViewById(R.id.stop_point_name_suggest);
        final TextView txtServiceID = view.findViewById(R.id.frame_service_type_suggest);
        final TextView txtAddress = view.findViewById(R.id.address_suggest);
        final TextView txtProvinceID = view.findViewById(R.id.frame_province_type_suggest);
        final TextView txtMinCost = view.findViewById(R.id.min_cost_suggest);
        final TextView txtMaxCost = view.findViewById(R.id.max_cost_suggest);

        txtSpName.setText(suggestedPointInfo.getName());
        txtAddress.setText(suggestedPointInfo.getAddress());
        txtMinCost.setText(Long.toString(suggestedPointInfo.getMinCost()));
        txtMaxCost.setText(Long.toString(suggestedPointInfo.getMaxCost()));

        String[] province = getResources().getStringArray(R.array.province);
        String[] serviceType = getResources().getStringArray(R.array.serviceName);
        int indexSvId = suggestedPointInfo.getServiceTypeId() - 1;
        int indexProvinceId = suggestedPointInfo.getProvinceId() - 1;

        if (indexSvId >= serviceType.length || indexSvId < 0){
            indexSvId = 0;
        }
        if (indexProvinceId >= province.length || indexProvinceId < 0){
            indexProvinceId = 0;
        }
        txtServiceID.setText(serviceType[indexSvId]);
        txtProvinceID.setText(province[indexProvinceId]);

        //Cancel button
        final ImageButton cancelbtn = view.findViewById(R.id.cancel_add_suggest);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //Set arrive time picker
        final TextView arriveTimeText = view.findViewById(R.id.arrive_time_suggest);
        ImageButton setTimeBtn =  view.findViewById(R.id.arrive_time_btn_suggest);
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
                                    arriveTimeText.setText(dateFormat.format(dateFormat.parse(time)));
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
        //Set arrive date picker
        final TextView arriveDateText = view.findViewById(R.id.arrive_date_suggest);
        ImageButton arriveDateBtn = view.findViewById(R.id.arrive_date_btn_suggest);
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
                                    arriveDateText.setText(dateFormat.format(dateFormat.parse(date)));
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

        //Set leave date picker
        final TextView leaveDateText = view.findViewById(R.id.leave_date_suggest);
        ImageButton leaveDateBtn = view.findViewById(R.id.leave_date_btn_suggest);
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
                                    leaveDateText.setText(dateFormat.format(dateFormat.parse(date)));
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

        //Set leave time picker
        final TextView leaveTimeText = view.findViewById(R.id.leave_time_suggest);
        ImageButton leaveTimeBtn =  view.findViewById(R.id.leave_time_btn_suggest);
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
                                    leaveTimeText.setText(dateFormat.format(dateFormat.parse(time)));
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

        Button submitBtn = view.findViewById(R.id.add_sp_submit_suggest);
        //arriveDateText, arriveTimeText, leaveDateText, leaveTimeText, address

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strArrTime = arriveTimeText.getText().toString();
                String strArrDate = arriveDateText.getText().toString();
                String strLeaveTime = leaveTimeText.getText().toString();
                String strLeaveDate = leaveDateText.getText().toString();

                //Check isEmpty in fields: name, date, address
                boolean isValid = true;
                if (TextUtils.isEmpty(strArrTime)){
                    arriveTimeText.setError("Please choose Arrive Time");
                    isValid = false;
                }
                if (TextUtils.isEmpty(strArrDate)){
                    arriveDateText.setError("Please choose Arrive Date");
                    isValid = false;
                }
                if (TextUtils.isEmpty(strLeaveTime)){
                    leaveTimeText.setError("Please choose Leave Time");
                    isValid = false;
                }
                if (TextUtils.isEmpty(strLeaveDate)){
                    leaveDateText.setError("Please choose Leave Date");
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

                //Send to a StopPointInfo object
                suggestedPointInfo.setArriveAt(millisArriveDate);
                suggestedPointInfo.setLeaveAt(millisLeaveDate);
                //Create new instance of point info that contain id = null, serviceId = suggestedPointInfo.getId() to send to api
                StopPointInfo toSend = new StopPointInfo(
                        suggestedPointInfo.getName(),
                        suggestedPointInfo.getAddress(),
                        suggestedPointInfo.getProvinceId(),
                        suggestedPointInfo.getLat(),
                        suggestedPointInfo.getLongitude(),
                        suggestedPointInfo.getArriveAt(),
                        suggestedPointInfo.getLeaveAt(),
                        suggestedPointInfo.getServiceTypeId(),
                        suggestedPointInfo.getMinCost(),
                        suggestedPointInfo.getMaxCost(),
                        suggestedPointInfo.getId()
                );
                listener.applyDataSuggest(toSend);
                listener.fixedMarkerSuggest(new LatLng(Double.parseDouble(suggestedPointInfo.getLat()), Double.parseDouble(suggestedPointInfo.getLongitude())), suggestedPointInfo.getName(), suggestedPointInfo.getServiceTypeId());
                dismiss();
            }
        });

        builder.setView(view);

        return builder.create();
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddStopPointSuggestDialog.AddStopPointDialogSuggestListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement DialogListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Set transparent background and no title
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public interface AddStopPointDialogSuggestListener{
        void applyDataSuggest(StopPointInfo stopPointInfo);
        void fixedMarkerSuggest(LatLng position, String spName, int serviceTypeId);

    }
}
