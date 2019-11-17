package com.example.travelguide.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.example.travelguide.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

public class ListTourFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManger;

    private View.OnClickListener createTour = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent creatTourIntent = new Intent(getActivity(), CreateTourActivity.class);
            startActivity(creatTourIntent);
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_list_tour, container, false);
        final SearchView searchView = (SearchView) view.findViewById(R.id.tour_searchv);
        searchView.onActionViewExpanded();
        searchView.setIconified(true);



        //Set click on create tour button
        FloatingActionButton createTourBtn = (FloatingActionButton) view.findViewById(R.id.create_tour_btn);
        createTourBtn.setOnClickListener(createTour);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                searchView.clearFocus();
            }
        }, 300);

        //get token from login
        SharedPreferences sharedPref = getContext().getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        final String accessToken = sharedPref.getString(getString(R.string.saved_access_token),null);

        final ArrayList<TourItem> tourItems = new ArrayList<>();
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        String url="http://35.197.153.192:3000/tour/list?rowPerPage=20&pageNum=1";
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Res: ", response.toString());
                try {
                    TextView tv= (TextView) view.findViewById(R.id.tour_num);
                    tv.setText(response.getString("total"));

                     JSONArray listTour = response.getJSONArray("tours");
                     for (int i=0;i < listTour.length(); i++){
                         JSONObject o = listTour.getJSONObject(i);
                         String img = o.getString("avatar");
                         if (img == null){
//                             img = "@drawable/alternative_view";
                         }
                         String location = o.getString("name");

                         String startDate;
                         long milisStartDate = o.optLong("startDate", 0);
                         if (milisStartDate == 0){
                             startDate = "null";
                         }
                         else {
                             Calendar calendar = Calendar.getInstance();
                             DateFormat simple = new SimpleDateFormat("dd/MM/yyyy");
                             Date result = new Date(milisStartDate);
                             startDate = simple.format(result);
                         }
                         String endDate;
                         long milisEndDate = o.optLong("startDate", 0);
                         if (milisEndDate == 0){
                             endDate = "null";
                         }
                         else {
                             Calendar calendar = Calendar.getInstance();
                             DateFormat simple = new SimpleDateFormat("dd/MM/yyyy");
                             Date result = new Date(milisEndDate);
                             endDate = simple.format(result);
                         }
                         String date = startDate + " - "+ endDate;
                         String numAdults = o.getString("adults");
                         String numChilds = o.getString("childs");
                         String quantity;
                         if (numChilds.equals("0")){
                             quantity = numAdults + " adults";
                         }
                         else{
                             quantity = numAdults + " adults, " + numChilds + " childs";
                         }
                         String price = o.getString("minCost") + " - " + o.getString("maxCost");
                         tourItems.add(new TourItem(R.drawable.alternative_view, location, date, quantity, price));
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

        mRecyclerView = view.findViewById(R.id.rview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManger = new LinearLayoutManager(getActivity());

        mAdapter = new RecyclerDataAdapter(tourItems);

        mRecyclerView.setLayoutManager(mLayoutManger);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }
}