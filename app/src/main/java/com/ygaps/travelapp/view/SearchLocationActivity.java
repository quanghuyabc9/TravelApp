package com.ygaps.travelapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
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

import static com.facebook.FacebookSdk.getApplicationContext;

public class SearchLocationActivity extends AppCompatActivity {

    String accessToken;

    //Custom action bar
    View actionBarView;

    //Notify notfoundany
    TextView notFound;

    ArrayList<StopPointInfo> stopPointInfos;
    ArrayList<StopPointInfo> foundSp;
    ListViewResultLocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_location);

        //get token from login
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.shared_pref_name), 0);
        accessToken = sharedPref.getString(getString(R.string.saved_access_token),null);

        //Set custom action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_search_view_actionbar);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.back_button);
        Bitmap bitmap = ((BitmapDrawable) upArrow).getBitmap();
        upArrow = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 35, 35, true));
        actionBar.setHomeAsUpIndicator(upArrow);

        actionBarView = actionBar.getCustomView();

        //List view
        final ListView foundLv = findViewById(R.id.list_result_search_location);
        stopPointInfos = new ArrayList<StopPointInfo>();
        adapter = new ListViewResultLocationAdapter(this, stopPointInfos);
        foundLv.setAdapter(adapter);

        //Set on item clicked
        foundLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idSp = stopPointInfos.get(position).getId();
                Intent intent = new Intent();
                //put stop point's id back to father activity
                intent.putExtra("id", idSp);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        //
        notFound = findViewById(R.id.no_found_search_location);

        //get all point when query is empty
        getPoints("");

        //Set search view for place
        final SearchView searchView = actionBarView.findViewById(R.id.search_location);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getPoints(newText);
                return false;
            }
        });

    }

    private void getPoints(String query){
        OkHttpClient okHttpClient = new OkHttpClient();
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(Constants.APIEndpoint + "/tour/search/service?pageIndex=1&pageSize=100&searchKey="+query)
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
                    foundSp = new ArrayList<>();
                    try {
                        responseJSON = new JSONObject(jsonResBody);
                        JSONArray jsonArrayListSPI = responseJSON.getJSONArray("stopPoints");
                        foundSp = new Gson().fromJson(jsonArrayListSPI.toString(), new TypeToken<ArrayList<StopPointInfo>>(){}.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable()  {
                        @Override
                        public void run() {
                            stopPointInfos.clear();
                            stopPointInfos.addAll(foundSp);
                            adapter.notifyDataSetChanged();
                            if (stopPointInfos.size() == 0){
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
}
