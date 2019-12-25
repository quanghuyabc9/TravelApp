package com.ygaps.travelapp.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import static com.facebook.FacebookSdk.getApplicationContext;

public class TourDetailCommentsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecyclerDataAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText cmd;
    private Button btn_send;

    View view;

    public TourDetailCommentsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tourdetail_comments, container, false);

        cmd=view.findViewById(R.id.invitedChat);
        btn_send=view.findViewById(R.id.btn_send);

        final String tourId="4506";
        //get token from login
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        final String accessToken = sharedPref.getString(getString(R.string.saved_access_token), null);
        //show list-comment
        final ArrayList<CommentItem> commentItems = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String URL="http://35.197.153.192:3000/tour/comment-list";
        Number pageIndex=1;
        String pageSize="200";
        JSONObject object=new JSONObject();
        try {
            object.put("tourId", tourId);
            object.put("pageIndex",pageIndex);
            object.put("pageSize",pageSize);
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL,object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Res: ", response.toString());
                try {
                    JSONArray listComment = response.getJSONArray("commentList");
                    for (int i = 0; i < listComment.length(); i++) {
                        JSONObject o = listComment.getJSONObject(i);
                        String comment = o.getString("comment");

                        String name = o.getString("name");
                        if (name == null) {
                            name = "user";
                        }

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
                        commentItems.add(new CommentItem(name,comment,onTime));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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

        mRecyclerView=view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getActivity());

        mAdapter = new RecyclerDataAdapter(commentItems);
        mRecyclerView.setLayoutManager(layoutManager);


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
