package com.ygaps.travelapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ygaps.travelapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.fullyInitialize;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.ygaps.travelapp.utils.DateTimeTool.convertMillisToDateTime;

public class StopPointDialogTab1 extends Fragment {

    private StopPointInfo pointInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_stop_point_tab1, container, false);

        //get token from login
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        final String accessToken = sharedPref.getString(getString(R.string.saved_access_token),null);

        Bundle bundle = getArguments();
        String JSONPointInfo = bundle.getString("JSONPointInfo");
        pointInfo = new Gson().fromJson(JSONPointInfo, new TypeToken<StopPointInfo>(){}.getType());

        final TextView txtSpName = view.findViewById(R.id.sp_name_explore);
        final android.widget.TextView txtServiceID = view.findViewById(R.id.stop_point_type_explore);
        final TextView txtAddress = view.findViewById(R.id.stop_point_address_explore);
        final TextView txtPrice = view.findViewById(R.id.stop_point_price_explore);
        final TextView txtProvinceID = view.findViewById(R.id.stop_point_province_explore);
        final ImageView iconServiceType = view.findViewById(R.id.icon_service_type_explore);

        txtSpName.setText(pointInfo.getName());
        txtAddress.setText(pointInfo.getAddress());

        txtPrice.setText(pointInfo.getMinCost() + " - " + pointInfo.getMaxCost());

        String[] province = getResources().getStringArray(R.array.province);
        String[] serviceType = getResources().getStringArray(R.array.serviceName);
        int indexSvId = pointInfo.getServiceTypeId() - 1;
        int indexProvinceId = pointInfo.getProvinceId() - 1;
        if (indexSvId >= serviceType.length || indexSvId < 0){
            indexSvId = 0;
        }
        if (indexProvinceId >= province.length || indexProvinceId < 0){
            indexProvinceId = 0;
        }

        switch (indexSvId){
            case 0:
                iconServiceType.setImageResource(R.drawable.icons8_restaurant_100);
                break;
            case 1:
                iconServiceType.setImageResource(R.drawable.icons8_hotel_building_100);
                break;
            case 2:
                iconServiceType.setImageResource(R.drawable.icons8_empty_bed_96);
                break;
            case 3:
                iconServiceType.setImageResource(R.drawable.icons8_service_mark_80);
                break;
        }

        txtServiceID.setText(serviceType[indexSvId]);
        txtProvinceID.setText(province[indexProvinceId]);


        final RatingBar mRatingBar = view.findViewById(R.id.rating_bar_review_explore_tab1);
        final RelativeLayout mContainerFeedback=view.findViewById(R.id.container_edit_feedback_review_explore);
        final TextView mRatingScale = view.findViewById(R.id.tvRatingScale_explore);
        final EditText mFeedback = view.findViewById(R.id.edit_feedback_review_explore);
        final Button mSendFeedback = view.findViewById(R.id.btnSubmit_review_explore_tab1);
        final LinearLayout mContainerInfo= view.findViewById(R.id.container_info_explore_tab1);
        final Button mCancelSendFb = view.findViewById(R.id.btnCancelSubmit_review_explore_tab1);
        mCancelSendFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRatingScale.setVisibility(View.GONE);
                mContainerFeedback.setVisibility(View.GONE);
                mFeedback.setVisibility(View.GONE);
                mSendFeedback.setVisibility(View.GONE);
                mContainerInfo.setVisibility(View.VISIBLE);
                mCancelSendFb.setVisibility(View.GONE);
            }
        });

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mRatingScale.setVisibility(View.VISIBLE);
                mContainerFeedback.setVisibility(View.VISIBLE);
                mFeedback.setVisibility(View.VISIBLE);
                mSendFeedback.setVisibility(View.VISIBLE);
                mContainerInfo.setVisibility(View.GONE);
                mCancelSendFb.setVisibility(View.VISIBLE);
                mRatingScale.setText(String.valueOf(v));
                switch ((int) ratingBar.getRating()) {
                    case 1:
                        mRatingScale.setText("Very bad");
                        break;
                    case 2:
                        mRatingScale.setText("Need some improvement");
                        break;
                    case 3:
                        mRatingScale.setText("Good");
                        break;
                    case 4:
                        mRatingScale.setText("Great");
                        break;
                    case 5:
                        mRatingScale.setText("Awesome. I love it");
                        break;
                    default:
                        mRatingScale.setText("");
                }
            }
        });

        mSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFeedback.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill in feedback text box", Toast.LENGTH_LONG).show();
                } else {

                    RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
                    String url="http://35.197.153.192:3000/tour/add/feedback-service";
                    //Create request's body
                    JSONObject jsonBody = new JSONObject();
                    try {

                        jsonBody.put("serviceId", pointInfo.getId());
                        jsonBody.put("feedback", mFeedback.getText());
                        jsonBody.put("point", mRatingBar.getRating());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //Set request
                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mFeedback.setText("");
                            mRatingScale.setVisibility(View.GONE);
                            mContainerFeedback.setVisibility(View.GONE);
                            mFeedback.setVisibility(View.GONE);
                            mSendFeedback.setVisibility(View.GONE);
                            mContainerInfo.setVisibility(View.VISIBLE);
                            mCancelSendFb.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Thank you for sharing your feedback", Toast.LENGTH_SHORT).show();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Could not send feed back! Something went wrong", Toast.LENGTH_SHORT).show();

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
            }
        });

        //Get statistic of feedback service
        final RatingBar ratingBarIndicator = view.findViewById(R.id.rating_num_of_stars_explore);
        final TextView txtNumStars = view.findViewById(R.id.num_of_stars_explore);
        final ProgressBar progressBar1 = view.findViewById(R.id.progressBar1);
        final ProgressBar progressBar2 = view.findViewById(R.id.progressBar2);
        final ProgressBar progressBar3 = view.findViewById(R.id.progressBar3);
        final ProgressBar progressBar4 = view.findViewById(R.id.progressBar4);
        final ProgressBar progressBar5 = view.findViewById(R.id.progressBar5);
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        String url="http://35.197.153.192:3000/tour/get/feedback-point-stats?serviceId="+pointInfo.getId();
        //Set request
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Res: ", response.toString());
                ArrayList<Integer> arrayStars = new ArrayList<>();
                try {
                    JSONArray arrayStarsJson = response.getJSONArray("pointStats");
                    float averageStars = 0;
                    float sumStars = 0;
                    for (int i=0; i<5; i++){
                        arrayStars.add(Integer.parseInt(arrayStarsJson.getJSONObject(i).optString("total", "0")));

                    }
                    for (int i=0; i<5; i++){
                        sumStars += arrayStars.get(i);
                        averageStars += (i+1)*arrayStars.get(i);
                    }
                    if (sumStars != 0) {
                        averageStars /= sumStars;

                        int maxStars = Collections.max(arrayStars);
                        int progress1 = Math.round(arrayStars.get(0)*100/maxStars) ;
                        progressBar1.setProgress(progress1);
                        int progress2 = Math.round(arrayStars.get(1)*100/maxStars) ;
                        progressBar2.setProgress(progress2);
                        int progress3 = Math.round(arrayStars.get(2)*100/maxStars) ;
                        progressBar3.setProgress(progress3);
                        int progress4 = Math.round(arrayStars.get(3)*100/maxStars) ;
                        progressBar4.setProgress(progress4);
                        int progress5 = Math.round(arrayStars.get(4)*100/maxStars) ;
                        progressBar5.setProgress(progress5);
                    }
                    else {
                        progressBar1.setProgress(0);
                        progressBar2.setProgress(0);
                        progressBar3.setProgress(0);
                        progressBar4.setProgress(0);
                        progressBar5.setProgress(0);

                    }
                    ratingBarIndicator.setRating(averageStars);
                    DecimalFormat df = new DecimalFormat("#.#");
                    df.setRoundingMode(RoundingMode.HALF_UP);
                    txtNumStars.setText(df.format(averageStars));



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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


        return view;
    }
}
