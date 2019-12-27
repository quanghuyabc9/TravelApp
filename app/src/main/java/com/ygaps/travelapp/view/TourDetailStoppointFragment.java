package com.ygaps.travelapp.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.manager.Constants;
import com.ygaps.travelapp.utils.DateTimeTool;
import com.ygaps.travelapp.utils.EditTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class TourDetailStoppointFragment extends Fragment{

    View view;
    private String tourId = null;
    String authorization = null;

    private ArrayList<StopPointInfo> dataItems;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManger;
    private RecyclerStopPointTourDetailAdapter mAdapter;
    private StopPointTourDetailDialog stopPointTourDetailDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tourdetail_stoppoints, container, false);
        // Initialize tour detail data here
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
        authorization = sharedPreferences.getString(getString(R.string.saved_access_token), null);
        tourId = getArguments().getString("TourId");

        getListStopPoint();

        return view;
    }

    private void getListStopPoint(){
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
                    String jsonString = "";
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("stopPoints");
                        jsonString = jsonArray.toString();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dataItems = new Gson().fromJson(jsonString, new TypeToken<ArrayList<StopPointInfo>>(){}.getType());

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerView = view.findViewById(R.id.rview_list_sp_tour_detail);
                            mRecyclerView.setHasFixedSize(true);
                            mLayoutManger = new LinearLayoutManager(getActivity());

                            mAdapter = new RecyclerStopPointTourDetailAdapter(dataItems);
                            mAdapter.setOnItemClickListener(new RecyclerStopPointTourDetailAdapter.ClickListener() {
                                @Override
                                public void onItemClick(int position, View v) {

                                    stopPointTourDetailDialog = new StopPointTourDetailDialog();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("TourId", tourId);
                                    bundle.putString("JSONPointInfo", new Gson().toJsonTree(dataItems.get(position)).getAsJsonObject().toString());

                                    stopPointTourDetailDialog.setArguments(bundle);
                                    stopPointTourDetailDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

                                    stopPointTourDetailDialog.show(getChildFragmentManager(), "Stop point tour detail dialog");
                                }

                                @Override
                                public void onItemLongClick(int position, View v) {

                                }
                            });

                            mRecyclerView.setLayoutManager(mLayoutManger);

                            mRecyclerView.setAdapter(mAdapter);

                            if (dataItems.size() == 0){
                                TextView txtEmpty = view.findViewById(R.id.empty_list_review_explore_tab2);
                                txtEmpty.setVisibility(View.VISIBLE);
                            }

                        }
                    });

                    //final String tourname_data = jsonObject.getString("name");

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
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (stopPointTourDetailDialog!= null){
                stopPointTourDetailDialog.dismiss();
            }
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}
