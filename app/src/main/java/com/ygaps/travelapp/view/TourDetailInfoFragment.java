package com.ygaps.travelapp.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.manager.Constants;
import com.ygaps.travelapp.utils.DateTimeTool;
import com.ygaps.travelapp.utils.EditTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TourDetailInfoFragment extends Fragment {

    View view;
    // Ui reference
    private  TextView textView_date,
             textView_people,
             textView_cost,
             textView_security;
    TextView editButton;
    TextView showReviewsButton;

    // Tour detail data
    String authorization = null;
    private String tourId = null,
                   tourName = null;


    public TourDetailInfoFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tourdetail_info, container, false);
        // Initialize tour detail data here
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
        authorization = sharedPreferences.getString(getString(R.string.saved_access_token), null);
        //tourId = "300";
        tourId = getArguments().getString("TourId");
        tourName = getArguments().getString("TourName");
        // Get view
        textView_date = view.findViewById(R.id.textview_tourdetail_editinfo_date);
        textView_people = view.findViewById(R.id.textview_tourdetail_editinfo_people);
        textView_cost = view.findViewById(R.id.textview_tourdetail_editinfo_cost);
        textView_security = view.findViewById(R.id.textview_tourdetail_editinfo_security);
        showReviewsButton = view.findViewById(R.id.show_tour_review);
        editButton = view.findViewById(R.id.imagebutton_tourdetail_editinfo_editbtn);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view.getContext(), TourDetailEditInfoActivity.class);
                intent.putExtra("TourId", tourId);
                intent.putExtra("TourName", tourName);
                startActivity(intent);
            }
        });
        showReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListReviewsTourDialog listReviewsTourDialog = new ListReviewsTourDialog();
                Bundle bundle = new Bundle();
                bundle.putString("TourId", tourId);
                listReviewsTourDialog.setArguments(bundle);
                listReviewsTourDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

                listReviewsTourDialog.show(getChildFragmentManager(), "List reviews tour dialog");
            }
        });

        getTourInfo();

        final RatingBar mRatingBar = view.findViewById(R.id.rating_bar_review_explore_tab1);
        final RelativeLayout mContainerFeedback=view.findViewById(R.id.container_edit_feedback_review_explore);
        final TextView mRatingScale = view.findViewById(R.id.tvRatingScale_explore);
        final EditText mFeedback = view.findViewById(R.id.edit_feedback_review_explore);
        final Button mSendFeedback = view.findViewById(R.id.btnSubmit_review_explore_tab1);
        final Button mCancelSendFb = view.findViewById(R.id.btnCancelSubmit_review_explore_tab1);
        mCancelSendFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRatingScale.setVisibility(View.GONE);
                mContainerFeedback.setVisibility(View.GONE);
                mFeedback.setVisibility(View.GONE);
                mSendFeedback.setVisibility(View.GONE);
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
        final Fragment mFragment = this;
        mSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFeedback.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill in feedback text box", Toast.LENGTH_LONG).show();
                } else {

                    RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
                    String url="http://35.197.153.192:3000/tour/add/review";
                    //Create request's body
                    JSONObject jsonBody = new JSONObject();
                    try {
                        jsonBody.put("tourId", tourId);
                        jsonBody.put("point", mRatingBar.getRating());
                        jsonBody.put("review", mFeedback.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Set request
                    JsonObjectRequest req = new JsonObjectRequest(com.android.volley.Request.Method.POST, url, jsonBody, new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mFeedback.setText("");
                            mRatingScale.setVisibility(View.GONE);
                            mContainerFeedback.setVisibility(View.GONE);
                            mFeedback.setVisibility(View.GONE);
                            mSendFeedback.setVisibility(View.GONE);
                            mCancelSendFb.setVisibility(View.GONE);
                            getTourStatics();
                            Toast.makeText(getContext(), "Thanks for sharing your feedback", Toast.LENGTH_LONG).show();

                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Could not send feedback! Something went wrong", Toast.LENGTH_SHORT).show();

                        }
                    }){
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Authorization",authorization);
                            return headers;
                        }
                    };
                    //Add request to Queue
                    requestQueue.add(req);

                }
            }
        });

        getTourStatics();


        return view;
    }

    private void getTourInfo() {
        if (authorization == null || tourId == null)
            return;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constants.APIEndpoint + "/tour/info" + "?tourId=" + tourId)
                .addHeader("Authorization", authorization)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final IOException fe = e;
                ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(view.getContext(), fe.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().string());
                        //final String tourname_data = jsonObject.getString("name");

                        final long milis_startDate_data = jsonObject.getLong("startDate"),
                                   milis_endDate_data = jsonObject.getLong("endDate");

                        String  startDate_data = DateTimeTool.convertMillisToDateTime(milis_startDate_data),
                                endDate_data = DateTimeTool.convertMillisToDateTime(milis_endDate_data);

                        final String startDate_dataFixed = startDate_data.split(" ")[0];
                        final String endDate_dataFixed = endDate_data.split(" ")[0];

                        final String adults_data = jsonObject.getString("adults"),
                                     children_data = jsonObject.getString("childs"),
                                     minCost_data = jsonObject.getString("minCost"),
                                     maxCost_data = jsonObject.getString("maxCost");
                        final String isPrivate_data = jsonObject.getString("isPrivate");

                        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final String date = startDate_dataFixed + " - " + endDate_dataFixed;
                                final String people = adults_data + " adults" + " - " + children_data + " childs";
                                final String cost = minCost_data + " - " + maxCost_data;
                                String security = "Public";
                                if (isPrivate_data.equals("true"))
                                    security = "Private";
                                textView_date.setText(date);
                                textView_people.setText(people);
                                textView_cost.setText(cost);
                                textView_security.setText(security);
                            }
                        });
                    } catch (JSONException e) {
                        final JSONException fe = e;
                        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(), fe.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else if (response.code() == 404 || response.code() == 500) {
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().toString());
                        final String message = jsonObject.getString("message");
                        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                        final JSONException fe = e;
                        ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(view.getContext(), fe.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    ((Activity) view.getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(view.getContext(), getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
    private void getTourStatics(){
        //Get statistic of feedback service
        final RatingBar ratingBarIndicator = view.findViewById(R.id.rating_num_of_stars_explore);
        final TextView txtNumStars = view.findViewById(R.id.num_of_stars_explore);
        final ProgressBar progressBar1 = view.findViewById(R.id.progressBar1);
        final ProgressBar progressBar2 = view.findViewById(R.id.progressBar2);
        final ProgressBar progressBar3 = view.findViewById(R.id.progressBar3);
        final ProgressBar progressBar4 = view.findViewById(R.id.progressBar4);
        final ProgressBar progressBar5 = view.findViewById(R.id.progressBar5);
        RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
        String url="http://35.197.153.192:3000/tour/get/review-point-stats?tourId="+tourId;
        //Set request
        JsonObjectRequest req = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
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
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization",authorization);
                return headers;
            }
        };
        //Add request to Queue
        requestQueue.add(req);
    }
}