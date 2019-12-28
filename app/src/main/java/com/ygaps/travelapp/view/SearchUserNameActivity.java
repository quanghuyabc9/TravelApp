package com.ygaps.travelapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.manager.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchUserNameActivity extends AppCompatActivity {

    String accessToken;

    String tourId;
    boolean isInvited;

    //Custom action bar
    View actionBarView;

    //Notify notfoundany
    TextView notFound;

    ArrayList<UserInfo> userInfos;
    ArrayList<UserInfo> foundUser;
    ListViewAdapterSearchUser adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user_name);

        //get token from login
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_name), 0);
        accessToken = sharedPref.getString(getString(R.string.saved_access_token),null);
        notFound = findViewById(R.id.no_found_search_location);

        tourId = getIntent().getExtras().getString("TourId");

        //Set custom action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_search_user_actionbar);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.back_button);
        Bitmap bitmap = ((BitmapDrawable) upArrow).getBitmap();
        upArrow = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 35, 35, true));
        actionBar.setHomeAsUpIndicator(upArrow);

        actionBarView = actionBar.getCustomView();

        getIsPrivate();
        //get all point when query is empty
        getUsers("");

        //List view
        final ListView foundLv = findViewById(R.id.list_result_search_user);
        userInfos = new ArrayList<>();
        adapter = new ListViewAdapterSearchUser(this, userInfos);
        foundLv.setAdapter(adapter);

        //Set on item clicked
        foundLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {


                RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
                String url="http://35.197.153.192:3000/tour/add/member";
                //Create request's body
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("tourId", tourId);
                    jsonBody.put("invitedUserId", userInfos.get(position).getUserId());
                    jsonBody.put("isInvited", Boolean.toString(true));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("body", jsonBody.toString());
                //Set request
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        Toast.makeText(getApplicationContext(), "Invitation was sent to "+ userInfos.get(position).getFullName(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchUserNameActivity.this, "Server error on adding member to a tour", Toast.LENGTH_SHORT).show();

                    }
                }){
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization",accessToken);
                        return headers;
                    }
                };
                //Add request to Queue
                requestQueue.add(req);
            }
        });


        //Set search view for place
        final SearchView searchView = actionBarView.findViewById(R.id.search_location);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getUsers(newText);
                return false;
            }
        });

    }

    private void getUsers(String query){
        OkHttpClient okHttpClient = new OkHttpClient();
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(Constants.APIEndpoint + "/user/search?pageIndex=1&pageSize=100&searchKey="+query)
                .addHeader("Authorization", accessToken)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {

            }
            @Override
            public void onResponse(final com.squareup.okhttp.Response response) throws IOException {
                if(response.code() == 200){

                    final String jsonResBody = response.body().string();
                    Log.d("jsonResBody", jsonResBody);
                    JSONObject responseJSON = null;
                    foundUser = new ArrayList<>();
                    try {
                        responseJSON = new JSONObject(jsonResBody);
                        JSONArray jsonArrayListSPI = responseJSON.getJSONArray("users");
                        foundUser = new Gson().fromJson(jsonArrayListSPI.toString(), new TypeToken<ArrayList<UserInfo>>(){}.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable()  {
                        @Override
                        public void run() {
                            userInfos.clear();
                            userInfos.addAll(foundUser);

                            adapter.notifyDataSetChanged();
                            if (userInfos.size() == 0){
                                notFound.setVisibility(View.VISIBLE);
                            }
                            else {
                                notFound.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return true;
    }
    private void getIsPrivate(){
        OkHttpClient okHttpClient = new OkHttpClient();
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(Constants.APIEndpoint + "/tour/info")
                .addHeader("Authorization", accessToken)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {

            }
            @Override
            public void onResponse(final com.squareup.okhttp.Response response) throws IOException {
                if(response.code() == 200){
                    final String jsonResBody = response.body().string();
                    JSONObject responseJSON = null;
                    try {
                        responseJSON = new JSONObject(jsonResBody);
                        isInvited = responseJSON.getBoolean("isInvited");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
