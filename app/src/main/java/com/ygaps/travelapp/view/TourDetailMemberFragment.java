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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.utils.EditTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

public class TourDetailMemberFragment extends Fragment {
    private EditText name;
    private Button confirm;
    private int userId;

    View view;

    public TourDetailMemberFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tourdetail_member, container, false);
<<<<<<< HEAD

        //Get userId
        name= view.findViewById(R.id.invitedUser);

        confirm=view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String userName = name.getText().toString();
                String pageSize = "1";
                Number pageIndex=1;

                //Gửi request lên server để nhận idUser
                RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());
                String url = "http://35.197.153.192:3000/user/search";

                //create request pagram
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("searchKey", userName);
                    jsonBody.put("pageIndex", pageIndex);
                    jsonBody.put("pageSize", pageSize);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //set request
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Res: ", response.toString());
                        try {
                            userId = response.getInt("id");
                            Toast.makeText(v.getContext(), "get userId", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(v.getContext(), "fail getTour", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // VolleyLog.d("Err", "Error: " + error.getMessage());
                        // Log.e("Err", "Site Info Error: " + error.getMessage());
                    }
                });
                requestQueue.add(req);
                //get token from login
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
                final String accessToken = sharedPref.getString(getString(R.string.saved_access_token), null);
                //Gửi request lên server để thêm thành thêm thành viên.
                RequestQueue requestQueue1 = Volley.newRequestQueue(v.getContext());
                String url1 = "http://35.197.153.192:3000/tour/add/member";
                String tourId="3925";
               // String userInviteId=name.getText().toString();
               // final Boolean isInvite=false;
                //create request pagram
                JSONObject jsonBody1 = new JSONObject();
                try {
                    jsonBody1.put("invitedUserId",userId);
                    jsonBody1.put("tourId", tourId);
                    jsonBody1.put("isInvited", false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //set request
                JsonObjectRequest req1 = new JsonObjectRequest(Request.Method.POST, url1, jsonBody1, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Res: ", response.toString());
                        try {
                            String createdOn = response.getString("createdOn");
                            Toast.makeText(v.getContext(), "success !!!", Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(v.getContext(), "fail1 !!!", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(v.getContext(), "fail 2 !!!", Toast.LENGTH_SHORT).show();
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

=======
        EditTool.HideSoftKeyboard(view.getContext());
>>>>>>> e3374517491f207f4b9e43194caf3f9dd9be0ed8
        return view;
    }
}
