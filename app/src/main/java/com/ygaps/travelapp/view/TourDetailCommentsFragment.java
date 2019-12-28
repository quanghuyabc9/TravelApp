package com.ygaps.travelapp.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.utils.EditTool;

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

import retrofit2.http.HEAD;

import static com.facebook.FacebookSdk.getApplicationContext;

public class TourDetailCommentsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManger;
    private ArrayList<CommentTourInfo> commentTourInfos;
    private  View view;

    private EditText cmd;
    private TextView btn_send;
    private String tourId;
    private boolean isHost;

    private RelativeLayout containerSend;

    private int userId;
    private String accessToken;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       view = inflater.inflate(R.layout.fragment_tourdetail_comments, container, false);

        cmd=view.findViewById(R.id.invitedChat);
        btn_send=view.findViewById(R.id.btn_send);
        containerSend = view.findViewById(R.id.send);

        commentTourInfos = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManger = new LinearLayoutManager(getActivity());
        //Set event button report click
        mAdapter = new RecyclerTourCommentsAdapter(commentTourInfos);

        mRecyclerView.setLayoutManager(mLayoutManger);
        mRecyclerView.setAdapter(mAdapter);


        //get token from login
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        accessToken = sharedPref.getString(getString(R.string.saved_access_token), null);
        //show list-comment
        Bundle bundle = getArguments();
        tourId = bundle.getString("TourId");
        isHost = bundle.getBoolean("IsHost");
        //
        if (!isHost){
            containerSend.setVisibility(View.GONE);
        }

        getListComment();

        //get Id user
        getUserId();


        //send comment
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String comment = cmd.getText().toString();


                //Gui request len server
                //Gửi request lên server để thêm thành thêm thành viên.
                RequestQueue requestQueue1 = Volley.newRequestQueue(v.getContext());
                String url = "http://35.197.153.192:3000/tour/comment";

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("tourId", tourId);
                    jsonBody.put("userId", userId);
                    jsonBody.put("comment", comment);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //set request
                JsonObjectRequest req1 = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        cmd.setText("");
                        getListComment();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Err", "Error: " + error.getMessage());
                        Log.e("Err", "Site Info Error: " + error.getMessage());
                        Toast.makeText(v.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", accessToken);
                        return headers;
                    }
                };
                requestQueue1.add(req1);
            }
        });

        return view;
    }
    private void getListComment(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        String url="http://35.197.153.192:3000/tour/comment-list?tourId="+tourId+"&pageIndex=1&pageSize=100";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray feedbackList = response.getJSONArray("commentList");
                    ArrayList<CommentTourInfo> newCommentTourInfos = new Gson().fromJson(feedbackList.toString(), new TypeToken<ArrayList<CommentTourInfo>>(){}.getType());
                    commentTourInfos.clear();
                    commentTourInfos.addAll(newCommentTourInfos);
                    mAdapter.notifyDataSetChanged();

                    if (commentTourInfos.size() == 0){
                        TextView txtEmpty = view.findViewById(R.id.empty_list_review_explore_tab2);
                        txtEmpty.setVisibility(View.VISIBLE);
                    }
                    else {
                        TextView txtEmpty = view.findViewById(R.id.empty_list_review_explore_tab2);
                        txtEmpty.setVisibility(View.GONE);
                    }

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
    }
    private void getUserId(){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        String url="http://35.197.153.192:3000/user/info";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    userId = response.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                userId = 1;
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
    }

}
