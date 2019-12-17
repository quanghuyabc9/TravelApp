package com.ygaps.travelapp.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ygaps.travelapp.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListStopPointDialog extends AppCompatDialogFragment {

    private ArrayList<StopPointInfo> dataItems;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManger;
    private RecyclerView.Adapter mAdapter;

    private ListStopPointDialogListener listener;

    private JSONArray jsonDataArray;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_list_stop_point_layout,null);

        String JsonDataString = getArguments().getString("JSONData");
        dataItems = new Gson().fromJson(JsonDataString, new TypeToken<ArrayList<StopPointInfo>>(){}.getType());
//        try {
//            JSONArray JsonData = new JSONArray(JsonDataString);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        mRecyclerView = view.findViewById(R.id.rv_list_sp_dialog);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManger = new LinearLayoutManager(getActivity());

        mAdapter = new RecyclerStopPointsDataAdapter(dataItems);

        mRecyclerView.setLayoutManager(mLayoutManger);
        //Add divider line
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));

        mRecyclerView.setAdapter(mAdapter);

        final ImageButton cancelbtn = view.findViewById(R.id.cancel_list_stop_point);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final Button confirmBtn = view.findViewById(R.id.sp_list_confirm);

        if (dataItems.size() == 0){
            confirmBtn.setBackground(getResources().getDrawable(R.drawable.rounded_button_disabled));
            confirmBtn.setText("No Stop Points set");

        }
        else {
            try {
                jsonDataArray = new JSONArray(JsonDataString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            confirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.confirm(jsonDataArray);
                }
            });
        }

        builder.setView(view);
        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);
        try {
            listener = (ListStopPointDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement Dialog Listener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public interface ListStopPointDialogListener{
        void confirm(JSONArray jsonArray);
    }
}

