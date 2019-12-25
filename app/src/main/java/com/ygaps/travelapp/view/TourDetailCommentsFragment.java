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
import com.google.gson.JsonObject;
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
    private RecyclerDataAdapter1 mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText cmd;
    private Button btn_send;
    private String tourId;

    ArrayList<CommentItem> commentItems;
    ArrayList<CommentItem> holderCommentItems;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       final View view = inflater.inflate(R.layout.fragment_tourdetail_comments, container, false);

        cmd=view.findViewById(R.id.invitedChat);
        btn_send=view.findViewById(R.id.btn_send);


        //get token from login
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        final String accessToken = sharedPref.getString(getString(R.string.saved_access_token), null);
        tourId ="4506";
        //show list-comment
        commentItems=new ArrayList<>();
        holderCommentItems=new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        String URL="http://35.197.153.192:3000/tour/comment-list?tourId=4506&pageIndex=1&pageSize=200";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Toast.makeText(view.getContext(), "get success", Toast.LENGTH_SHORT).show();
                    JSONArray listComment = response.getJSONArray("commentList");
                    for (int i = 0; i < listComment.length(); i++) {
                        JSONObject o = listComment.getJSONObject(i);
                        String comment = o.getString("comment");

                        String name = o.getString("name");

                        String onTime;
                        long milisTime = o.optLong("createdOn", 0);
                        if (milisTime == 0) {
                            onTime = "null";
                        } else {
                            Calendar calendar = Calendar.getInstance();
                            DateFormat simple = new SimpleDateFormat("dd/MM/yyyy");
                            Date result = new Date(milisTime);
                            onTime = simple.format(result);
                        }
                        commentItems.add(new CommentItem(name, comment, onTime));
                    }
                    holderCommentItems.addAll(commentItems);
                    mRecyclerView=view.findViewById(R.id.recycler_view);
                    mRecyclerView.setHasFixedSize(true);
                    layoutManager=new LinearLayoutManager(getActivity());
                    mAdapter=new RecyclerDataAdapter1(commentItems);

                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setAdapter(mAdapter);
                }catch (JSONException e){
                    Toast.makeText(view.getContext(), "get fail", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(view.getContext(), "get error", Toast.LENGTH_SHORT).show();
                VolleyLog.d("Err", "Error: " + error.getMessage());
                Log.e("Err", "Site Info Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", accessToken);
                return headers;
            }
        };
        requestQueue.add(request);




        //send comment
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String comment = cmd.getText().toString();
                String userId = "677";


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
                        Log.d("Res: ", response.toString());
                        try {
                            String createdOn = response.getString("createdOn");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
}
