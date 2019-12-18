package com.ygaps.travelapp.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.ygaps.travelapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import static android.widget.ArrayAdapter.createFromResource;
import static com.ygaps.travelapp.utils.DateTimeTool.convertMillisToDateTime;

public class InfoStopPointDialog extends AppCompatDialogFragment {
    private InfoStopPointDialog.InfoStopPointDialogListener listener;

    private StopPointInfo pointInfo;
    private int index;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_stop_point_info, null);

        Bundle bundle = getArguments();
        String JsonDataString = bundle.getString("JSONPointInfo");
        index = bundle.getInt("Index");
        pointInfo = new Gson().fromJson(JsonDataString, new TypeToken<StopPointInfo>(){}.getType());

        final TextView txtSpName = view.findViewById(R.id.stop_point_name_info);
        final TextView txtServiceID = view.findViewById(R.id.frame_service_type_info);
        final TextView txtAddress = view.findViewById(R.id.address_info);
        final TextView txtProvinceID = view.findViewById(R.id.frame_province_type_info);
        final TextView txtMinCost = view.findViewById(R.id.min_cost_info);
        final TextView txtMaxCost = view.findViewById(R.id.max_cost_info);
        final TextView txtArrive = view.findViewById(R.id.arrive_datetime_info);
        final TextView txtLeave = view.findViewById(R.id.leave_datetime_info);

        txtSpName.setText(pointInfo.getName());
        txtAddress.setText(pointInfo.getAddress());
        txtMinCost.setText(Long.toString(pointInfo.getMinCost()));
        txtMaxCost.setText(Long.toString(pointInfo.getMaxCost()));

        txtArrive.setText(convertMillisToDateTime(pointInfo.getArriveAt()));
        txtLeave.setText(convertMillisToDateTime(pointInfo.getLeaveAt()));

        String[] province = getResources().getStringArray(R.array.province);
        String[] serviceType = getResources().getStringArray(R.array.serviceName);
        int indexSvId = pointInfo.getServiceTypeId() - 1;
        int indexProvinceId = pointInfo.getProvinceId() - 1;
        if (indexSvId > serviceType.length || indexSvId <0){
            indexSvId = 0;
        }
        if (indexProvinceId > province.length || indexProvinceId < 0){
            indexProvinceId = 0;
        }
        txtServiceID.setText(serviceType[indexSvId]);
        txtProvinceID.setText(province[indexProvinceId]);

        //Cancel button
        final ImageButton cancelbtn = view.findViewById(R.id.cancel_add_info);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        final Button deleteBtn = view.findViewById(R.id.add_sp_delete_info);
        //arriveDateText, arriveTimeText, leaveDateText, leaveTimeText, address

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteStopPoint(index);
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
            listener = (InfoStopPointDialog.InfoStopPointDialogListener) context;
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


    public interface InfoStopPointDialogListener{
        void deleteStopPoint(int index);
    }
}