package com.ygaps.travelapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ygaps.travelapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StopPointDialogTab2 extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManger;
    private ArrayList<ReviewsInfo> reviewsInfoItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_stop_point_tab2, container, false);

        //get token from login
        SharedPreferences sharedPref = getContext().getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        final String accessToken = sharedPref.getString(getString(R.string.saved_access_token),null);

        Bundle bundle = getArguments();
        int serviceId = bundle.getInt("serviceId");

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        String url="http://35.197.153.192:3000/tour/get/feedback-service?serviceId="+serviceId+"&pageIndex=1&pageSize=100";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Res: ", response.toString());
                try {
                    JSONArray feedbackList = response.getJSONArray("feedbackList");
                    reviewsInfoItems = new Gson().fromJson(feedbackList.toString(), new TypeToken<ArrayList<ReviewsInfo>>(){}.getType());

                    if (reviewsInfoItems.size() == 0){
                        TextView txtEmpty = view.findViewById(R.id.empty_list_review_explore_tab2);
                        txtEmpty.setVisibility(View.VISIBLE);
                    }

                    mRecyclerView = view.findViewById(R.id.rview_reviews_explore);
                    mRecyclerView.setHasFixedSize(true);
                    mLayoutManger = new LinearLayoutManager(getActivity());
                    //Set event button report click
                    mAdapter = new RecyclerStopPointsReviewsAdapter(reviewsInfoItems, new RecyclerStopPointsReviewsAdapter.RecyclerStopPointsReviewsAdapterListener() {
                        @Override
                        public void iconButtonReportOnClick(View v, final int position) {
                            final View mView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_report_layout, null);
                            final PopupWindow popUp = new PopupWindow(mView, 200, LinearLayout.LayoutParams.WRAP_CONTENT, false);
                            popUp.setTouchable(true);
                            popUp.setFocusable(true);
                            popUp.setOutsideTouchable(true);

                            //Solution
                            popUp.showAsDropDown(v.findViewById(R.id.btn_report_review_explore), 0,2, Gravity.BOTTOM);
                            ((TextView) mView.findViewById(R.id.cancel_popup_review_explore)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popUp.dismiss();
                                }
                            });
                            ((TextView) mView.findViewById(R.id.send_report_review_explore)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                                    JSONObject jsonBody = new JSONObject();
                                    try {
                                        jsonBody.put("feedbackId", reviewsInfoItems.get(position).getId());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    String url = "http://35.197.153.192:3000/tour/report/feedback";
                                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Log.d("Res: ", response.toString());
                                            Toast.makeText(getActivity(),"Report sent", Toast.LENGTH_SHORT).show();
                                            popUp.dismiss();
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            VolleyLog.d("Err", "Error: " + error.getMessage());
                                            Log.e("Err", "Site Info Error: " + error.getMessage());
                                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                            popUp.dismiss();

                                        }
                                    }) {
                                        @Override
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            HashMap<String, String> headers = new HashMap<String, String>();
                                            headers.put("Authorization", accessToken);
                                            return headers;
                                        }
                                    };

                                    requestQueue.add(req);

                                }
                            });



                        }
                    });

                    mRecyclerView.setLayoutManager(mLayoutManger);
                    mRecyclerView.setAdapter(mAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Err", "Error: " + error.getMessage());
                Log.e("Err", "Site Info Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization",accessToken);
                return headers;
            }
        };

        requestQueue.add(req);

        return view;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}