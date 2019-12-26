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
import android.util.Log;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ygaps.travelapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.ygaps.travelapp.utils.DateTimeTool.convertMillisToDateTime;

public class UpdateSuggestStopPointDialog extends AppCompatDialogFragment {
    private UpdateSuggestStopPointDialog.InfoSuggestStopPointDialogListener listener;

    private StopPointInfo pointInfo;
    private int index;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_update_suggest_stop_point, null);

        Bundle bundle = getArguments();
        String JsonDataString = bundle.getString("JSONPointInfo");
        index = bundle.getInt("Index");
        pointInfo = new Gson().fromJson(JsonDataString, new TypeToken<StopPointInfo>(){}.getType());

        final TextView txtSpName = view.findViewById(R.id.stop_point_name_suggest_update);
        final TextView txtServiceID = view.findViewById(R.id.frame_service_type_suggest_update);
        final TextView txtAddress = view.findViewById(R.id.address_suggest_update);
        final TextView txtProvinceID = view.findViewById(R.id.frame_province_type_suggest_update);

        final TextView txtMinCost = view.findViewById(R.id.min_cost_suggest_update);
        final TextView txtMaxCost = view.findViewById(R.id.max_cost_suggest_update);

        final TextView txtArriveDate = view.findViewById(R.id.arrive_date_suggest_upđate);
        final TextView txtArriveTime = view.findViewById(R.id.arrive_time_suggest_update);

        final TextView txtLeaveDate = view.findViewById(R.id.leave_date_suggest_update);
        final TextView txtLeaveTime = view.findViewById(R.id.leave_time_suggest_update);

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
        int indexSvId = pointInfo.getServiceTypeId() - 1;
        int indexProvinceId = pointInfo.getProvinceId() - 1;

        if (indexSvId >= serviceType.length || indexSvId < 0){
            indexSvId = 0;
        }
        if (indexProvinceId >= province.length || indexProvinceId < 0){
            indexProvinceId = 0;
        }
        txtServiceID.setText(serviceType[indexSvId]);
        txtProvinceID.setText(province[indexProvinceId]);


        final ImageButton cancelbtn = view.findViewById(R.id.cancel_suggest_update);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //Set arrive time picker
        ImageButton setTimeBtn =  view.findViewById(R.id.arrive_time_btn_suggest_update);
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
        //Set arrive date picker
        ImageButton arriveDateBtn = view.findViewById(R.id.arrive_date_btn_suggest_update);
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

        //Set leave date picker
        ImageButton leaveDateBtn = view.findViewById(R.id.leave_date_btn_suggest_update);
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

        //Set leave time picker
        ImageButton leaveTimeBtn =  view.findViewById(R.id.leave_time_btn_suggest_update);
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


        Button updateButton = view.findViewById(R.id.button_suggest_update);
        //arriveDateText, arriveTimeText, leaveDateText, leaveTimeText, address

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strArrTime = txtArriveTime.getText().toString();
                String strArrDate = txtArriveDate.getText().toString();
                String strLeaveTime = txtLeaveTime.getText().toString();
                String strLeaveDate = txtLeaveDate.getText().toString();

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
                StopPointInfo updatePoint = new StopPointInfo(
                        pointInfo.getName(),
                        pointInfo.getAddress(),
                        pointInfo.getProvinceId(),
                        pointInfo.getLat(),
                        pointInfo.getLongitude(),
                        millisArriveDate,
                        millisLeaveDate,
                        pointInfo.getServiceTypeId(),
                        pointInfo.getMinCost(),
                        pointInfo.getMaxCost()
                );
                listener.updateSuggestStopPoint(index, updatePoint);
                dismiss();
            }
        });



        final Button deleteBtn = view.findViewById(R.id.button_delete_suggest_update);
        //arriveDateText, arriveTimeText, leaveDateText, leaveTimeText, address

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteSuggestStopPoint(index);
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
            listener = (UpdateSuggestStopPointDialog.InfoSuggestStopPointDialogListener) context;
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


    public interface InfoSuggestStopPointDialogListener{
        void deleteSuggestStopPoint(int index);
        void updateSuggestStopPoint(int index, StopPointInfo updatePoint);
    }
}