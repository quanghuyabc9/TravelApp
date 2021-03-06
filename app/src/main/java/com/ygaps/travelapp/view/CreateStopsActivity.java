package com.ygaps.travelapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.manager.Constants;
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
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class CreateStopsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        AddStopPointDialog.AddStopPointDialogListener,
        ListStopPointDialog.ListStopPointDialogListener,
        AddStopPointSuggestDialog.AddStopPointDialogSuggestListener,
        UpdateStopPointDialog.InfoStopPointDialogListener,
        UpdateSuggestStopPointDialog.InfoSuggestStopPointDialogListener,
        com.google.android.gms.location.LocationListener
{

    private GoogleApiClient googleApiClient;

    private String accessToken;
    //Map
    SupportMapFragment mapFragment;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    //Custom action bar
    View actionBarView;

    //Alert Dialog when click Cancel
    AlertDialog alertDialog;
    AlertDialog.Builder builder;

    //List Stop Points
    private ArrayList<StopPointInfo> listSP;
    private ArrayList<Marker> listSPMarker;

    //marker
    private Marker myMarker;
    private String MarkerAddress;

    //List suggest destination marker around
    private ArrayList<StopPointInfo> listSuggestPointInfo;

    // Declare a variable for the cluster manager.
    private ClusterManager<MyMarkerItem> mClusterManager;

    public final int Request_User_Location_Code = 99;

    GoogleMap.OnMarkerClickListener eventMarkerClicked = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            int index;
            if (myMarker != null && marker.getId().equals(myMarker.getId())) {
                AddStopPointDialog addStopPointDialog = new AddStopPointDialog();
                Bundle bundle = new Bundle();
                LatLng latLng = marker.getPosition();
                bundle.putString("Address", MarkerAddress);
                bundle.putDouble("Latitude", latLng.latitude);
                bundle.putDouble("Longitude", latLng.longitude);
                addStopPointDialog.setArguments(bundle);
                addStopPointDialog.show(getSupportFragmentManager(), "Add stop point dialog");
            }
            else if ((index = indexMarkerInSPMarker(marker)) != -1){
                Bundle bundle = new Bundle();

                StopPointInfo itemInListSP = listSP.get(index);
                bundle.putString("JSONPointInfo", new Gson().toJsonTree(itemInListSP).getAsJsonObject().toString());
                bundle.putInt("Index", index);

                if (itemInListSP.getServiceId() == 0) {
                    UpdateStopPointDialog updateStopPointDialog = new UpdateStopPointDialog();
                    updateStopPointDialog.setArguments(bundle);
                    updateStopPointDialog.show(getSupportFragmentManager(), "Update stop point dialog");
                }
                else{
                    UpdateSuggestStopPointDialog updateSuggestStopPointDialog = new UpdateSuggestStopPointDialog();
                    updateSuggestStopPointDialog.setArguments(bundle);
                    updateSuggestStopPointDialog.show(getSupportFragmentManager(), "Update stop point dialog");
                }
            }
            else {
                mGoogleMap.setOnMarkerClickListener(mClusterManager);
                mClusterManager.onMarkerClick(marker);
                mGoogleMap.setOnMarkerClickListener(eventMarkerClicked);
            }
            return false;
        }
    };

    public static final MediaType JSON  = MediaType.parse("application/json; charset=utf-8");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get token from login
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref_name), 0);
        accessToken = sharedPref.getString(getString(R.string.saved_access_token),null);


        //Init map
        if(googleServicesAvailable()){
            setContentView(R.layout.activity_create_stops);
            checkUserLocationPermission();
            initMap();
        }
        else
        {
            Toast.makeText(this, "Failed to initmap!!!", Toast.LENGTH_SHORT).show();
        }
        listSuggestPointInfo = new ArrayList<>();
        mClusterManager = new ClusterManager<MyMarkerItem>(CreateStopsActivity.this, mGoogleMap);

        listSP = new ArrayList<>();
        listSPMarker = new ArrayList<>();
        FloatingActionButton listSPButton = findViewById(R.id.list_sp);
        listSPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListStopPointDialog listStopPointDialog = new ListStopPointDialog();
                Bundle bundle = new Bundle();
                bundle.putString("JSONData", new Gson().toJsonTree(listSP).getAsJsonArray().toString());
                listStopPointDialog.setArguments(bundle);
                listStopPointDialog.show(getSupportFragmentManager(), "List stop point dialog");
            }
        });
        //Set custom action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_create_stop_point_action_bar);
        actionBarView = actionBar.getCustomView();

        //Set search view for place
        final RelativeLayout searchView = actionBarView.findViewById(R.id.search_location_create);

        searchView.setOnClickListener(new SearchView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateStopsActivity.this, SearchLocationActivity.class);

                //intent.putExtra("title",foundItems.get(i).lvTitle );

                startActivityForResult(intent,2);
            }
        });

        //Set Confirm exit dialog
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("This tour will be discarded.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String url="http://35.197.153.192:3000/tour/update-tour";
                //Create request's body
                JSONObject jsonBody = new JSONObject();
                try {
                    Intent intent = getIntent();
                    int tourId = intent.getExtras().getInt("tourId");
                    jsonBody.put("id", tourId);
                    jsonBody.put("status", -1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Set request
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(CreateStopsActivity.this, "Deleted 1 tour", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        finish();
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
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Button cancelBtn = actionBarView.findViewById(R.id.cancel_create_stop);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

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
            getSuggestPoints();
            Log.d("Size", Integer.toString(listSuggestPointInfo.size()));


            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    setMarker(latLng.latitude, latLng.longitude);
                    Toast.makeText(CreateStopsActivity.this, MarkerAddress, Toast.LENGTH_LONG).show();
                }
            });

            mGoogleMap.setOnMarkerClickListener(eventMarkerClicked);
            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Geocoder gc = new Geocoder(CreateStopsActivity.this);

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
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                                .addApi(LocationServices.API)
//                                .addConnectionCallbacks(this)
//                                .addOnConnectionFailedListener(this)
//                                .build();
//        mGoogleApiClient.connect();

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

                    runOnUiThread(new Runnable()  {
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
        SearchView sv = findViewById(R.id.place_search);
        String location = query;

        Geocoder gc = new Geocoder(this);
        List<Address> list;
        try {
            list = gc.getFromLocationName(location,1);
            if (list.size() == 0){
                Toast.makeText(this, "No location match", Toast.LENGTH_LONG).show();
                return;
            }
            Address address = list.get(0);

            double lat = address.getLatitude();
            double lng = address.getLongitude();
            setMarker(lat, lng);
            Toast.makeText(this, MarkerAddress, Toast.LENGTH_LONG).show();
            goToLocationZoom(lat, lng,16);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
//        if (location == null){
//            Toast.makeText(this, "Can't get current location", Toast.LENGTH_LONG).show();
//        }
//        else
//        {
//            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
//            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 13);
//            mGoogleMap.animateCamera(update);
//        }
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

    @Override
    public void applyData(StopPointInfo stopPointInfo) {
        listSP.add(stopPointInfo);
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

    @Override
    public void fixedMarker(String spName, int serviceTypeId) {
        double lat = myMarker.getPosition().latitude,
                lng = myMarker.getPosition().longitude;
        Marker copy = mGoogleMap.addMarker(new MarkerOptions().draggable(false).title(spName).position(new LatLng(lat, lng)));
        listSPMarker.add(copy);
//        BitmapDrawable bitmapdraw;
//        int width = 40;
//        int height = 40;
//        switch (serviceTypeId){
//            case 1:
//                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icons8_place_marker_48);
//                copy.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width, height, false)));
//                break;
//            case 2:
//                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icons8_place_marker_48);
//                copy.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width, height, false)));
//                break;
//            case 3:
//                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icons8_place_marker_48);
//                copy.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width, height, false)));
//                break;
//            case 4:
//                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icons8_place_marker_48);
//                copy.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width, height, false)));
//                break;
//        }
        myMarker.remove();
    }


    @Override
    public void confirm(JSONArray jsonArray) {

        //Queue
        RequestQueue requestQueue = Volley.newRequestQueue(CreateStopsActivity.this);
        String url = "http://35.197.153.192:3000/tour/set-stop-points";
        //Json Body
        Intent intent = getIntent();
        int tourId = intent.getExtras().getInt("tourId");
        JSONObject jsonBody = new JSONObject();
        try {
            for (int i=0 ; i<listSP.size(); i++){
                jsonArray.getJSONObject(i).remove("id");
                if (jsonArray.getJSONObject(i).getInt("serviceId") == 0){
                    jsonArray.getJSONObject(i).remove("serviceId");
                }
            }
            jsonBody.put("tourId", tourId);
            jsonBody.put("stopPoints", jsonArray);
            //Log.d("REEEEQQQQ", jsonBody.toString());
            //Set request
            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("REEEES", response.toString());
                    Toast.makeText(CreateStopsActivity.this, "Added " + listSP.size() + " stop points to your tour", Toast.LENGTH_LONG).show();
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Err", "Error: " + error.getMessage());
                    Log.e("Err", "Site Info Error: " + error.getMessage());
                    Toast.makeText(CreateStopsActivity.this, "Failed to add stop points! Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization",accessToken);
                    return headers;
                }
            };
            requestQueue.add(req);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void setUpClusterer() {

        // Initialize the manager with the context and the map.
        // (Activity extends context, so we can pass 'this' in the constructor.)
        mClusterManager = new ClusterManager<MyMarkerItem>(this, mGoogleMap);

        // Point the map's listeners at the listeners implemented by the cluster manager.
        mGoogleMap.setOnCameraIdleListener(mClusterManager);

        // Add cluster items (markers) to the cluster manager.
        addItems();
    }

    private void addItems() {

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
                        AddStopPointSuggestDialog addStopPointSuggestDialog = new AddStopPointSuggestDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("JSONPointInfo", new Gson().toJsonTree(listSuggestPointInfo.get(item.getInfoPos())).getAsJsonObject().toString());
                        addStopPointSuggestDialog.setArguments(bundle);
                        addStopPointSuggestDialog.show(getSupportFragmentManager(), "Add stop point suggest dialog");
                        return false;
                    }
                }

                return false;
            }
        });
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyMarkerItem>() {
            @Override
            public boolean onClusterItemClick(MyMarkerItem myMarkerItem) {
                AddStopPointSuggestDialog addStopPointSuggestDialog = new AddStopPointSuggestDialog();
                Bundle bundle = new Bundle();
                bundle.putString("JSONPointInfo", new Gson().toJsonTree(listSuggestPointInfo.get(myMarkerItem.getInfoPos())).getAsJsonObject().toString());
                addStopPointSuggestDialog.setArguments(bundle);
                addStopPointSuggestDialog.show(getSupportFragmentManager(), "Add stop point suggest dialog");
                return false;
            }
        });
    }

    @Override
    public void applyDataSuggest(StopPointInfo stopPointInfo) {
        listSP.add(stopPointInfo);
    }

    @Override
    public void fixedMarkerSuggest(LatLng position, String spName, int serviceTypeId) {
        double lat = position.latitude,
                lng = position.longitude;
        Marker copy = mGoogleMap.addMarker(new MarkerOptions().draggable(false).title(spName).position(new LatLng(lat, lng)));
        listSPMarker.add(copy);
        //Collection<Marker> markerCollection = mClusterManager.getMarkerCollection().getMarkers();
//        BitmapDrawable bitmapdraw;
//        int width = 40;
//        int height = 40;
//        switch (serviceTypeId){
//            case 1:
//                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icons8_place_marker_48);
//                copy.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width, height, false)));
//                break;
//            case 2:
//                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icons8_place_marker_48);
//                copy.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width, height, false)));
//                break;
//            case 3:
//                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icons8_place_marker_48);
//                copy.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width, height, false)));
//                break;
//            case 4:
//                bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.icons8_place_marker_48);
//                copy.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width, height, false)));
//                break;
//        }
    }
    private int indexMarkerInSPMarker(Marker marker){
        for(int i = 0; i < listSPMarker.size(); i++){
            if(listSPMarker.get(i).getId().equals(marker.getId())){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void deleteStopPoint(int index) {
        listSP.remove(index);
        listSPMarker.get(index).remove();
        listSPMarker.remove(index);
        Toast.makeText(CreateStopsActivity.this, "Deleted 1 stop point", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateStopPoint(int index, StopPointInfo updatePoint) {
        StopPointInfo point =  listSP.get(index);
        point.setName(updatePoint.getName());
        point.setAddress(updatePoint.getAddress());
        point.setProvinceId(updatePoint.getProvinceId());
        point.setLat(updatePoint.getLat());
        point.setLongitude(updatePoint.getLongitude());
        point.setArriveAt(updatePoint.getArriveAt());
        point.setLeaveAt(updatePoint.getLeaveAt());
        point.setServiceTypeId(updatePoint.getServiceTypeId());
        point.setMinCost(updatePoint.getMinCost());
        point.setMaxCost(updatePoint.getMaxCost());
        listSPMarker.get(index).setTitle(updatePoint.getName());
        Toast.makeText(CreateStopsActivity.this, "Updated 1 stop point", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void deleteSuggestStopPoint(int index) {
        listSP.remove(index);
        listSPMarker.get(index).remove();
        listSPMarker.remove(index);
        Toast.makeText(CreateStopsActivity.this, "Deleted 1 stop point", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateSuggestStopPoint(int index, StopPointInfo updatePoint) {
        StopPointInfo point =  listSP.get(index);
        point.setArriveAt(updatePoint.getArriveAt());
        point.setLeaveAt(updatePoint.getLeaveAt());
        listSPMarker.get(index).setTitle(updatePoint.getName());
        Toast.makeText(CreateStopsActivity.this, "Updated 1 stop point", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking()
                && !event.isCanceled()) {
            alertDialog = builder.create();
            alertDialog.show();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

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
                        if (googleApiClient==null)
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
        googleApiClient = new GoogleApiClient.Builder( this )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();
        googleApiClient.connect();
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
                                AddStopPointSuggestDialog stopPointDialog = new AddStopPointSuggestDialog();
                                Bundle bundle = new Bundle();
                                bundle.putString("JSONPointInfo", new Gson().toJsonTree(cur).getAsJsonObject().toString());
                                stopPointDialog.setArguments(bundle);
                                stopPointDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

                                stopPointDialog.show(getSupportFragmentManager(), "Add stop point suggest dialog");
                            }
                        }, 1000);

                    }
                }

            }
        }
    }
}
