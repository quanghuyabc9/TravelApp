package com.ygaps.travelapp.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
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
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.manager.Constants;
import com.ygaps.travelapp.utils.EditTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;

public class TourDetailMemberFragment extends Fragment {
    private EditText name;
    private Button confirm;
    private TextView addMem;

    private int userId;
    private String tourId;
    private boolean isHost;

    View view;

    private String accessToken;
    private ArrayList<MembersInfo> membersInfo;
    private ArrayList<MembersInfo> newMembersInfo;
    private ListViewMembersAdapter mAdapter;

    public TourDetailMemberFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tourdetail_member, container, false);

        // Initialize tour detail data here
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences(getString(R.string.shared_pref_name), Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString(getString(R.string.saved_access_token), null);

        tourId = getArguments().getString("TourId");
        isHost = getArguments().getBoolean("IsHost");

        addMem = view.findViewById(R.id.add_member_textview);

        if (!isHost){
            addMem.setVisibility(View.GONE);
        }
        else {
            addMem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =  new Intent(getActivity(), SearchUserNameActivity.class);
                    intent.putExtra("TourId", tourId);
                    startActivityForResult(intent,2);
                }
            });
        }


        //List view
        final ListView listViewMember = view.findViewById(R.id.list_view_member);
        membersInfo = new ArrayList<MembersInfo>();
        mAdapter = new ListViewMembersAdapter(getActivity(), membersInfo);
        listViewMember.setAdapter(mAdapter);

//
//        //Set on item clicked
//        foundLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                int idSp = stopPointInfos.get(position).getId();
//                Intent intent = new Intent();
//                //put stop point's id back to father activity
//                intent.putExtra("id", idSp);
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        });

        //
        //get all point when query is empty
        getMemberList();


        //Get userId
        name = view.findViewById(R.id.invitedUser);

        return view;
    }

    private void getMemberList(){
        OkHttpClient okHttpClient = new OkHttpClient();
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(Constants.APIEndpoint + "/tour/info?tourId="+tourId)
                .addHeader("Authorization", accessToken)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {

            }
            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                if(response.code() == 200){

                    final String jsonResBody = response.body().string();
                    JSONObject responseJSON = null;
                    newMembersInfo = new ArrayList<>();
                    try {
                        responseJSON = new JSONObject(jsonResBody);
                        JSONArray jsonArrayListSPI = responseJSON.getJSONArray("members");
                        newMembersInfo = new Gson().fromJson(jsonArrayListSPI.toString(), new TypeToken<ArrayList<MembersInfo>>(){}.getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getActivity().runOnUiThread(new Runnable()  {
                        @Override
                        public void run() {
                            membersInfo.clear();
                            membersInfo.addAll(newMembersInfo);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK){
            getMemberList();
        }
    }
}
