package com.ygaps.travelapp.view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.manager.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


public class ExploreFragment extends Fragment
        implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.location.LocationListener {
    private String accessToken;
    protected Activity mActivity;

    private GoogleApiClient googleApiClient;

    //Map
    SupportMapFragment mapFragment;
    GoogleMap mGoogleMap;
    //Custom action bar
    View actionBarView;

    //List suggest destination marker around
    private ArrayList<StopPointInfo> listSuggestPointInfo;

    //Declare a variable for the cluster manager.
    private ClusterManager<MyMarkerItem> mClusterManager;

    public final int Request_User_Location_Code = 99;

    View myview;

    public static final MediaType JSON  = MediaType.parse("application/json; charset=utf-8");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view;

        //get token from login
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        accessToken = sharedPref.getString(getString(R.string.saved_access_token),null);


        //Init map
        if(googleServicesAvailable()){
            view = inflater.inflate(R.layout.fragment_explore, container, false);
            myview = view;

            checkUserLocationPermission();
            initMap();

            //Set custom action bar
            ActionBar actionBar = ((AppCompatActivity)mActivity).getSupportActionBar();
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.custom_explore_actionbar);
            actionBarView = actionBar.getCustomView();

            //Set search view for place
            final RelativeLayout searchView = actionBarView.findViewById(R.id.search_location_explore);

            searchView.setOnClickListener(new SearchView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mActivity, SearchLocationActivity.class);

                    //intent.putExtra("title",foundItems.get(i).lvTitle );

                    startActivityForResult(intent,2);
                }
            });
            return view;
        }
        else
        {
            Toast.makeText(mActivity, "Failed to initmap!!!", Toast.LENGTH_SHORT).show();
        }
        listSuggestPointInfo = new ArrayList<>();
        mClusterManager = new ClusterManager<>(mActivity, mGoogleMap);

        return inflater.inflate(R.layout.fragment_explore, container, false);

    }
    //Init map
    private void initMap() {
        mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container).getChildFragmentManager().findFragmentById(R.id.map_explore);
        mapFragment.getMapAsync(this);
    }
    //Check google services
    public boolean googleServicesAvailable(){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(mActivity);
        if(isAvailable == ConnectionResult.SUCCESS)
        {
            return true;
        }
        else if (api.isUserResolvableError(isAvailable))
        {
            Dialog dialog = api.getErrorDialog(mActivity, isAvailable, 0);
            dialog.show();

        }
        else
        {
            Toast.makeText(mActivity,"Can't connect to play service", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        if (mGoogleMap != null)
        {
            //Disable Map Toolbar:
            mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
            listSuggestPointInfo = new ArrayList<>();
            getSuggestPoints();

            //mGoogleMap.setOnMarkerClickListener(eventMarkerClicked);
        }

        goToLocationZoom(10.763182, 106.682494, 12);

        if (ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(mActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            //Toast.makeText(mActivity, "Cannot access location", Toast.LENGTH_LONG).show();
        }

        final View locationButton = ((View) Objects.requireNonNull(mapFragment.getView()).findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

        if(locationButton != null)
            locationButton.setVisibility(View.GONE);
        myview.findViewById(R.id.my_cr_location_explore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGoogleMap != null)
                {
                    if(locationButton != null)
                        locationButton.callOnClick();

                }
            }
        });

    }

    private void getSuggestPoints() {

        JSONObject jsonBody = new JSONObject();
        OkHttpClient okHttpClient = new OkHttpClient();

        LatLng sw = new LatLng(23.457796, 101.802655);
        LatLng ne = new LatLng(8.553419,109.097577);

        try {
            JSONArray coordList = new JSONArray(
                    "[" +
                            "{" +
                            "\"coordinateSet\": [" +
                            "{" +
                            "\"lat\":" + sw.latitude + "," +
                            "\"long\":" + sw.longitude +
                            "}," +
                            "{" +
                            "\"lat\":" + ne.latitude + "," +
                            "\"long\":" + ne.longitude +
                            "}" +
                            "]" +
                            "}" +
                    "]"
            );
            jsonBody.put("hasOneCoordinate", false);
            jsonBody.put("coordList", coordList);
            //Log.d("jsonBody", jsonBody.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(JSON, jsonBody.toString());
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(Constants.APIEndpoint + "/tour/suggested-destination-list")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", accessToken)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {

            }
            @Override
            public void onResponse(final com.squareup.okhttp.Response response) throws IOException {
                if(response.code() == 200){

                    final String jsonResBody = response.body().string();

                    mActivity.runOnUiThread(new Runnable()  {
                        @Override
                        public void run() {
                            JSONObject responseJSON = null;
                            try {
                                responseJSON = new JSONObject(jsonResBody);
                                JSONArray jsonArrayListSPI = responseJSON.getJSONArray("stopPoints");
                                listSuggestPointInfo = new Gson().fromJson(jsonArrayListSPI.toString(), new TypeToken<ArrayList<StopPointInfo>>(){}.getType());
                                setUpClusterer();
                                mClusterManager.setRenderer(new OwnIconRendered(getApplicationContext(), mGoogleMap, mClusterManager));
                                mClusterManager.setAnimation(false);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    //geo locate google map
    public void geoLocate(String query){
        SearchView sv = myview.findViewById(R.id.place_search);
        String location = query;

        Geocoder gc = new Geocoder(mActivity);
        List<Address> list;
        try {
            list = gc.getFromLocationName(location,1);
            if (list.size() == 0){
                Toast.makeText(mActivity, "No location match", Toast.LENGTH_LONG).show();
                return;
            }
            Address address = list.get(0);

            double lat = address.getLatitude();
            double lng = address.getLongitude();
            goToLocationZoom(lat, lng,16);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void setUpClusterer() {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyMarkerItem>(mActivity, mGoogleMap);

        // Point the map's listeners at the listeners implemented by the cluster manager.
        mGoogleMap.setOnCameraIdleListener(mClusterManager);
        mGoogleMap.setOnMarkerClickListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {


        // Add ten cluster items in close proximity, for purposes of this example.
        //Add suggest makers to map
        for(int i = 0; i < listSuggestPointInfo.size(); i++){
            StopPointInfo cItem = listSuggestPointInfo.get(i);
            LatLng ll =  new LatLng(Double.parseDouble(cItem.getLat()), Double.parseDouble(cItem.getLongitude()));


            MyMarkerItem item = new MyMarkerItem(Double.parseDouble(cItem.getLat()), Double.parseDouble(cItem.getLongitude()), i);
            mClusterManager.addItem(item);

        }
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyMarkerItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyMarkerItem> cluster) {
                for (MyMarkerItem item: cluster.getItems()){
                    if (item.getPosition().equals(cluster.getPosition())){
                        StopPointDialog stopPointDialog = new StopPointDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("JSONPointInfo", new Gson().toJsonTree(listSuggestPointInfo.get(item.getInfoPos())).getAsJsonObject().toString());
                        stopPointDialog.setArguments(bundle);
                        stopPointDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

                        stopPointDialog.show(getChildFragmentManager(), "Stop point dialog");
                        return false;
                    }
                }

                return false;
            }
        });
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyMarkerItem>() {
            @Override
            public boolean onClusterItemClick(MyMarkerItem myMarkerItem) {
                StopPointDialog stopPointDialog = new StopPointDialog();
                Bundle bundle = new Bundle();
                bundle.putString("JSONPointInfo", new Gson().toJsonTree(listSuggestPointInfo.get(myMarkerItem.getInfoPos())).getAsJsonObject().toString());
                stopPointDialog.setArguments(bundle);
                stopPointDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

                stopPointDialog.show(getChildFragmentManager(), "Stop point dialog");
                return false;
            }
        });
    }

    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission( mActivity,Manifest.permission.ACCESS_FINE_LOCATION )!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale( mActivity,Manifest.permission.ACCESS_FINE_LOCATION )){
                ActivityCompat.requestPermissions( mActivity,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else {
                ActivityCompat.requestPermissions( mActivity,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode)
        {
            case Request_User_Location_Code:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (ContextCompat.checkSelfPermission( mActivity, Manifest.permission.ACCESS_FINE_LOCATION )==PackageManager.PERMISSION_GRANTED)
                    {
                        if (googleApiClient==null)
                        {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled( true );
                    }
                }
                else
                {
                        Toast.makeText(mActivity,"Permission Denied",Toast.LENGTH_SHORT ).show();
                }
                return;
        }
    }

    //On result searching
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK  && requestCode == 2){
            if (data.hasExtra("id")){
                int spId = data.getExtras().getInt("id");
                //Log.d("listSuggestPointInfo",Integer.toString(listSuggestPointInfo.size()) );
                for (int i=0; i<listSuggestPointInfo.size(); i++){
                    final StopPointInfo cur = listSuggestPointInfo.get(i);
                    if (cur.getId() == spId) {
                        LatLng ll = new LatLng(Double.parseDouble(cur.getLat()), Double.parseDouble(cur.getLongitude()));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, 12);
                        mGoogleMap.animateCamera(cameraUpdate);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                StopPointDialog stopPointDialog = new StopPointDialog();
                                Bundle bundle = new Bundle();
                                bundle.putString("JSONPointInfo", new Gson().toJsonTree(cur).getAsJsonObject().toString());
                                stopPointDialog.setArguments(bundle);
                                stopPointDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

                                stopPointDialog.show(getChildFragmentManager(), "Stop point dialog");
                            }
                        }, 1000);

                    }
                }

            }
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    protected synchronized void buildGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder( ExploreFragment.super.getContext())
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
