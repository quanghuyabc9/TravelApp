package com.ygaps.travelapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

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
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.manager.Constants;
import com.ygaps.travelapp.utils.DateTimeTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FollowTourActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener
{

    int userId;

    private String accessToken;
    //Map
    SupportMapFragment mapFragment;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    double myLatitude;
    double myLongitude;
    //Custom action bar
    View actionBarView;

    private String tourId;

    //Alert Dialog when click Cancel
    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    //List Stop Points
    private ArrayList<StopPointInfo> listSP;
    private ArrayList<Marker> listSPMarker;
    //marker speed warning
    private ArrayList<Marker> listWarningMarker;

    //marker
    private Marker myMarker;
    private String MarkerAddress;




    //list sp
    private ArrayList<MembersInfo> membersInfos;
    private ArrayList<Marker> listMarkerMem;


    public final int Request_User_Location_Code = 99;

//    GoogleMap.OnMarkerClickListener eventMarkerClicked = new GoogleMap.OnMarkerClickListener() {
//        @Override
//        public boolean onMarkerClick(Marker marker) {
//            int index;
//            if (myMarker != null && marker.getId().equals(myMarker.getId())) {
//                AddStopPointDialog addStopPointDialog = new AddStopPointDialog();
//                Bundle bundle = new Bundle();
//                LatLng latLng = marker.getPosition();
//                bundle.putString("Address", MarkerAddress);
//                bundle.putDouble("Latitude", latLng.latitude);
//                bundle.putDouble("Longitude", latLng.longitude);
//                addStopPointDialog.setArguments(bundle);
//                addStopPointDialog.show(getSupportFragmentManager(), "Add stop point dialog");
//            }
//            else if ((index = indexMarkerInSPMarker(marker)) != -1){
//                Bundle bundle = new Bundle();
//
//                StopPointInfo itemInListSP = listSP.get(index);
//                bundle.putString("JSONPointInfo", new Gson().toJsonTree(itemInListSP).getAsJsonObject().toString());
//                bundle.putInt("Index", index);
//
//                if (itemInListSP.getServiceId() == 0) {
//                    UpdateStopPointDialog updateStopPointDialog = new UpdateStopPointDialog();
//                    updateStopPointDialog.setArguments(bundle);
//                    updateStopPointDialog.show(getSupportFragmentManager(), "Update stop point dialog");
//                }
//                else{
//                    UpdateSuggestStopPointDialog updateSuggestStopPointDialog = new UpdateSuggestStopPointDialog();
//                    updateSuggestStopPointDialog.setArguments(bundle);
//                    updateSuggestStopPointDialog.show(getSupportFragmentManager(), "Update stop point dialog");
//                }
//            }
//            else {
//                mGoogleMap.setOnMarkerClickListener(mClusterManager);
//                mClusterManager.onMarkerClick(marker);
//                mGoogleMap.setOnMarkerClickListener(eventMarkerClicked);
//            }
//            return false;
//        }
//    };

    public static final MediaType JSON  = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get token from login
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        accessToken = sharedPref.getString(getString(R.string.saved_access_token),null);

        tourId = getIntent().getExtras().getString("TourId");
        Log.d("tourId", tourId);

        //Init map
        if(googleServicesAvailable()){
            setContentView(R.layout.activity_follow_tour);
            checkUserLocationPermission();
            initMap();
        }
        else
        {
            Toast.makeText(this, "Failed to initmap!!!", Toast.LENGTH_SHORT).show();
        }




        listSP = new ArrayList<>();
        listSPMarker = new ArrayList<>();
        listMarkerMem = new ArrayList<>();
        listWarningMarker = new ArrayList<>();
        //get stopPoint list from api get tour info
        getTourInfo();

        //Set event send message
        ImageButton sendMess = findViewById(R.id.button_send_message);
        final EditText contentMess = findViewById(R.id.edit_text_message);
        sendMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contentMess.getText().toString().isEmpty()){
                    return;
                }
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonBody = new JSONObject();

                try {
                    jsonBody.put("tourId", tourId);
                    jsonBody.put("userId", userId);
                    jsonBody.put("noti", contentMess.getText().toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(JSON, jsonBody.toString());
                com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                        .url(Constants.APIEndpoint + "/tour/notification")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", accessToken)
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        final IOException fe = e;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FollowTourActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.code() == 200) {
                            //Add tour's marker in map

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FollowTourActivity.this, "Your message was sent", Toast.LENGTH_SHORT).show();
                                    contentMess.setText("");
                                }
                            });

                        } else if (response.code() == 404 || response.code() == 500) {
                            try {
                                final JSONObject jsonObject = new JSONObject(response.body().toString());
                                final String message = jsonObject.getString("message");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FollowTourActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                final JSONException fe = e;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FollowTourActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FollowTourActivity.this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });


            }
        });

        //Set custom action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setCustomView(R.layout.custom_create_stop_point_action_bar);
//        actionBarView = actionBar.getCustomView();


    }
    //Init map
    private void initMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    //Check google services
    public boolean googleServicesAvailable(){
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if(isAvailable == ConnectionResult.SUCCESS)
        {
            return true;
        }
        else if (api.isUserResolvableError(isAvailable))
        {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();

        }
        else
        {
            Toast.makeText(this,"Can't connect to play service", Toast.LENGTH_SHORT).show();
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
            MarkerAddress = "";

//            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                @Override
//                public void onMapClick(LatLng latLng) {
//                    setMarker(latLng.latitude, latLng.longitude);
//                    Toast.makeText(FollowTourActivity.this, MarkerAddress, Toast.LENGTH_LONG).show();
//                }
//            });

//            mGoogleMap.setOnMarkerClickListener(eventMarkerClicked);
            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Geocoder gc = new Geocoder(FollowTourActivity.this);

                    LatLng ll = marker.getPosition();
                    List<Address> list = null;
                    MarkerAddress = getCompleteAddressString(ll.latitude, ll.longitude);
                    Toast.makeText(getApplicationContext(), MarkerAddress, Toast.LENGTH_SHORT).show();

                }
            });
        }

        goToLocationZoom(10.763182, 106.682494, 12);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            if (mGoogleApiClient==null)
            {
                buildGoogleApiClient();
            }

        } else {
            //Toast.makeText(getActivity(), "Cannot access location", Toast.LENGTH_LONG).show();
        }
        final View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

        if(locationButton != null)
            locationButton.setVisibility(View.GONE);
        findViewById(R.id.my_cr_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGoogleMap != null)
                {
                    if(locationButton != null)
                        locationButton.callOnClick();

                }
            }
        });



        FloatingActionButton sendSpeedWarning = findViewById(R.id.button_send_speed_warning);
        sendSpeedWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient();
                JSONObject jsonBody = new JSONObject();

                try {
                    jsonBody.put("lat", myLatitude);
                    jsonBody.put("long", myLongitude);
                    jsonBody.put("tourId", tourId);
                    jsonBody.put("userId", userId);
                    jsonBody.put("notificationType", 3);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(JSON, jsonBody.toString());
                com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                        .url(Constants.APIEndpoint + "/tour/add/notification-on-road")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", accessToken)
                        .post(requestBody)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        final IOException fe = e;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FollowTourActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.code() == 200) {
                            //Add tour's marker in map

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FollowTourActivity.this, "Your warning was sent", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else if (response.code() == 404 || response.code() == 500) {
                            try {
                                final JSONObject jsonObject = new JSONObject(response.body().toString());
                                final String message = jsonObject.getString("message");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FollowTourActivity.this, message, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (JSONException e) {
                                final JSONException fe = e;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FollowTourActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(FollowTourActivity.this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });

    }


    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.animateCamera(cameraUpdate);
    }


    private void setMarker(double lat, double lng) {
        if (myMarker != null){
            myMarker.remove();
        }
        MarkerAddress = getCompleteAddressString(lat, lng);
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.marker_icon_gray_png_27);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 65, 65, false));
        MarkerOptions options = new MarkerOptions().icon(bitmapDescriptor).draggable(true).position(new LatLng(lat, lng));
        myMarker = mGoogleMap.addMarker(options);
    }



//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int itemId = item.getItemId();
//        if (itemId == android.R.id.home) {
//            finish();
//        }
//        else {
//            switch (itemId) {
//                case R.id.mapTypeNormal:
//                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                    break;
//                case R.id.mapTypeSatellite:
//                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//                    break;
//                case R.id.mapTypeTerrain:
//                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//                    break;
//                case R.id.mapTypeHybrid:
//                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//                    break;
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }

    LocationRequest mLocationRequest;

    //Following current location
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null)
        {
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(2000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else{
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(2000);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null){
            Toast.makeText(this, "Can't get current location", Toast.LENGTH_LONG).show();
        }
        else
        {
            myLatitude = location.getLatitude();
            myLongitude = location.getLongitude();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
//            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 12);
//            mGoogleMap.animateCamera(update);
            if (listMarkerMem.size()!= 0){
                getCurrentUserCoordinate();
            }
        }
    }
    //Hide keyboard when click out side the search view
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private int indexMarkerInSPMarker(Marker marker){
        for(int i = 0; i < listSPMarker.size(); i++){
            if(listSPMarker.get(i).getId().equals(marker.getId())){
                return i;
            }
        }
        return -1;
    }

//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK
//                && event.getRepeatCount() == 0) {
//            event.startTracking();
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
//                && !event.isCanceled()) {
//            alertDialog = builder.create();
//            alertDialog.show();
//            return true;
//        }
//        return super.onKeyUp(keyCode, event);
//    }

    public boolean checkUserLocationPermission(){
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale( this,Manifest.permission.ACCESS_FINE_LOCATION )){
                ActivityCompat.requestPermissions( this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
            }
            else {
                ActivityCompat.requestPermissions( this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},Request_User_Location_Code);
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
                    if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION )==PackageManager.PERMISSION_GRANTED)
                    {
                        if (mGoogleApiClient==null)
                        {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled( true );
                    }
                }
                else
                {
                    Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT ).show();
                }
                return;
        }
    }
    protected synchronized void buildGoogleApiClient(){
        Toast.makeText(FollowTourActivity.this, "Build ApiClient", Toast.LENGTH_SHORT).show();
        mGoogleApiClient = new GoogleApiClient.Builder( this )
                .addApi( LocationServices.API )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .build();
        mGoogleApiClient.connect();
    }
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i));
                }
                strAdd = strReturnedAddress.toString();
                Log.w("location address", strReturnedAddress.toString());
            } else {
                Log.w("location address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("location address", "Cannot get Address!");
        }
        return strAdd;
    }
    private void getTourInfo() {
        if (accessToken == null || tourId == null)
            return;
        OkHttpClient client = new OkHttpClient();
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(Constants.APIEndpoint + "/tour/info" + "?tourId=" + tourId)
                .addHeader("Authorization", accessToken)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final IOException fe = e;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FollowTourActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    String jsonString = "";
                    String jsonStringMember = "";
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("stopPoints");
                        jsonString = jsonArray.toString();
                        JSONArray jsonArrayMember = jsonObject.getJSONArray("members");
                        jsonStringMember = jsonArrayMember.toString();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    listSP = new Gson().fromJson(jsonString, new TypeToken<ArrayList<StopPointInfo>>(){}.getType());

                    membersInfos = new Gson().fromJson(jsonStringMember, new TypeToken<ArrayList<MembersInfo>>(){}.getType());
                    //get user Id
                    getUserId();
                    //Add tour's marker in map

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setTourMarker();
                        }
                    });

                } else if (response.code() == 404 || response.code() == 500) {
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().toString());
                        final String message = jsonObject.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FollowTourActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                        final JSONException fe = e;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FollowTourActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FollowTourActivity.this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    private void setTourMarker(){
        Log.d("listSPSize", Integer.toString(listSP.size()));
        for (int i=0; i < listSP.size(); i++){
//            BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.marker_icon_gray_png_27);
//            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 65, 65, false));
//            MarkerOptions options = new MarkerOptions().icon(bitmapDescriptor).draggable(true).position(new LatLng(lat, lng));
            StopPointInfo curSP =listSP.get(i);
            MarkerOptions options = new MarkerOptions().title(curSP.getName()).draggable(false).position(new LatLng( Double.parseDouble(curSP.getLat()), Double.parseDouble(curSP.getLongitude())));
            Marker newMarker = mGoogleMap.addMarker(options);
            listSPMarker.add(newMarker);
        }

    }

    private void getUserId(){
        RequestQueue requestQueue = Volley.newRequestQueue(FollowTourActivity.this);

        String url="http://35.197.153.192:3000/user/info";
        JsonObjectRequest req = new JsonObjectRequest(com.android.volley.Request.Method.GET, url, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    userId = response.getInt("id");
                    Log.d("UserId", Integer.toString(userId));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getCurrentUserCoordinate();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                userId = 1;
                VolleyLog.d("Err", "Error: " + error.getMessage());
                Log.e("Err", "Site Info Error: " + error.getMessage());
                Toast.makeText(FollowTourActivity.this,
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

    }
    private void getCurrentUserCoordinate(){
        OkHttpClient client = new OkHttpClient();
        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("userId", userId);
            jsonBody.put("tourId", tourId);
            jsonBody.put("lat", myLatitude);
            jsonBody.put("long", myLongitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(JSON, jsonBody.toString());
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(Constants.APIEndpoint + "/tour/current-users-coordinate")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", accessToken)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final IOException fe = e;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FollowTourActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    final String jsonResBody = response.body().string();
//                    Log.d("SUCCCCCC", "OKKKKKKKKKKKKKKKKKKKKKKKKK");
//                    Log.d("SUCCCCCC", "jsonResBody");
//                    Log.d("UserId", Integer.toString(userId));
                    runOnUiThread(new Runnable()  {
                        @Override
                        public void run() {
                            JSONArray responseJSON = null;
                            try {
                                responseJSON = new JSONArray(jsonResBody);
                                listMarkerMem.clear();

                                for (int i = 0; i < responseJSON.length(); i++){
                                    String nameUser = getNameFromId(responseJSON.getJSONObject(i).getString("id"));
                                    MarkerOptions options = new MarkerOptions().title(nameUser).icon(bitmapDescriptorFromVector(FollowTourActivity.this, R.drawable.ic_account_circle_black_24dp)).draggable(false).position(new LatLng( responseJSON.getJSONObject(i).getDouble("lat"), responseJSON.getJSONObject(i).getDouble("long")));
                                    Marker newMarker = mGoogleMap.addMarker(options);
                                    listMarkerMem.add(newMarker);
                                    if (responseJSON.getJSONObject(i).getString("id").equals(Integer.toString(userId))){
                                        newMarker.setVisible(false);
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            getNotiSpeed();
                        }
                    });

                } else if (response.code() == 404 || response.code() == 500) {
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().toString());
                        final String message = jsonObject.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FollowTourActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                        final JSONException fe = e;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FollowTourActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FollowTourActivity.this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    private String getNameFromId(String id){
        for (int i = 0; i < membersInfos.size(); i++){
            if (Integer.toString(membersInfos.get(i).getId()).equals(id)){
                Log.d("Name in infor", Integer.toString(membersInfos.get(i).getId()));
                return membersInfos.get(i).getUserName();
            }
        }
        return "";
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void getNotiSpeed(){
        OkHttpClient client = new OkHttpClient();

        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(Constants.APIEndpoint + "/tour/adFd/notification-on-road?tourId="+tourId+"&pageIndex=1&pageSize=100")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", accessToken)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                final IOException fe = e;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FollowTourActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Response response) throws IOException {
                if (response.code() == 200) {
                    //Add tour's marker in map
                    final String jsonResBody = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONObject responseJSON = null;
                            JSONArray jsonArray = null;
                            try {
                                responseJSON = new JSONObject(jsonResBody);
                                jsonArray = responseJSON.getJSONArray("notiList");
                                listWarningMarker.clear();

                                for (int i = 0; i < jsonArray.length(); i++){
                                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icons8_distance_warning_100);
                                    BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 40, 40, false));
                                    MarkerOptions options = new MarkerOptions().icon(bitmapDescriptor).draggable(false).position(new LatLng( jsonArray.getJSONObject(i).getDouble("lat"), jsonArray.getJSONObject(i).getDouble("long")));
                                    Marker newMarker = mGoogleMap.addMarker(options);
                                    listWarningMarker.add(newMarker);

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } else if (response.code() == 404 || response.code() == 500) {
                    try {
                        final JSONObject jsonObject = new JSONObject(response.body().toString());
                        final String message = jsonObject.getString("message");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FollowTourActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (JSONException e) {
                        final JSONException fe = e;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FollowTourActivity.this, fe.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FollowTourActivity.this, getString(R.string.error_unknown), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }



}
