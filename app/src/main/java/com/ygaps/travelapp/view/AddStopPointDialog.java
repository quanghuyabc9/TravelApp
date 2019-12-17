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

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddStopPointDialog extends AppCompatDialogFragment {
    private AddStopPointDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_add_stop_point_layout, null);

        //Spinner service type
        final Spinner spinner1 = view.findViewById(R.id.spinner_service_type);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),R.array.serviceName, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        final RelativeLayout frameServiceType = view.findViewById(R.id.frame_service_type);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Spinner province
        final Spinner spinner2 = view.findViewById(R.id.spinner_province);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),R.array.province, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ImageButton cancelbtn = view.findViewById(R.id.cancel_add);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //Set arrive time picker
        final TextView arriveTimeText = view.findViewById(R.id.arrive_time);
        ImageButton setTimeBtn =  view.findViewById(R.id.arrive_time_btn);
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
        final TextView arriveDateText = view.findViewById(R.id.arrive_date);
        ImageButton arriveDateBtn = view.findViewById(R.id.arrive_date_btn);
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
        final TextView leaveDateText = view.findViewById(R.id.leave_date);
        ImageButton leaveDateBtn = view.findViewById(R.id.leave_date_btn);
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
        final TextView leaveTimeText = view.findViewById(R.id.leave_time);
        ImageButton leaveTimeBtn =  view.findViewById(R.id.leave_time_btn);
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

        //Set address text by data from activity
        final EditText edAddress = view.findViewById(R.id.address);
        Bundle bundle = getArguments();
        edAddress.setText(bundle.getString("Address", ""));
        //Set lat, lng
        final double latitude = bundle.getDouble("Latitude",0);
        final double longitude = bundle.getDouble("Longitude", 0);

        Button submitBtn = view.findViewById(R.id.add_sp_submit);
        final EditText edSpname = view.findViewById(R.id.stop_point_name);
        final EditText edMinCost = view.findViewById(R.id.min_cost);
        final EditText edMaxCost = view.findViewById(R.id.max_cost);
        //arriveDateText, arriveTimeText, leaveDateText, leaveTimeText, address

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String spname = edSpname.getText().toString();
                int serviceTypeId = spinner1.getSelectedItemPosition() + 1;
                String address = edAddress.getText().toString();
                int provinceId = spinner2.getSelectedItemPosition() + 1;

                String strMinCost = edMinCost.getText().toString();
                String strMaxCost = edMaxCost.getText().toString();
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

                String strArrTime = arriveTimeText.getText().toString();
                String strArrDate = arriveDateText.getText().toString();
                String strLeaveTime = leaveTimeText.getText().toString();
                String strLeaveDate = leaveDateText.getText().toString();

                //Check isEmpty in fields: name, date, address
                boolean isValid = true;
                if (TextUtils.isEmpty(spname)){
                    edSpname.setError("Please enter the name");
                    isValid = false;
                }
                if (TextUtils.isEmpty(address)){
                    edAddress.setError("Please enter address");
                    isValid = false;
                }
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
                StopPointInfo stopPointInfo = new StopPointInfo(spname, address, provinceId, Double.toString(latitude), Double.toString(longitude), millisArriveDate, millisLeaveDate, serviceTypeId, minCost, maxCost);
                listener.applyData(stopPointInfo);
                listener.fixedMarker(spname, serviceTypeId);
                dismiss();
            }
        });

//        builder.setTitle("Login").setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });
        builder.setView(view);

        return builder.create();
    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddStopPointDialogListener) context;
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


    public interface AddStopPointDialogListener{
        void applyData(StopPointInfo stopPointInfo);
        void fixedMarker(String spName, int serviceTypeId);

    }

}
