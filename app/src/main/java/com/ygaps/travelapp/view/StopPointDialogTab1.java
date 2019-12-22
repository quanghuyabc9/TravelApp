package com.ygaps.travelapp.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
        Log.d("Arrive", Long.toString(pointInfo.getArriveAt()));

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

                    //Send request create tour to server
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
                            Log.d("Res: ", response.toString());
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

        return view;
    }
}
