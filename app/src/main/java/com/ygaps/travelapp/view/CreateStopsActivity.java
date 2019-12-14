package com.ygaps.travelapp.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.ygaps.travelapp.R;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateStopsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, AddStopPointDialog.AddStopPointDialogListener {

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
    ArrayList<StopPointInfo> listSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Init map
        if(googleServicesAvailable()){
            setContentView(R.layout.activity_create_stops);

            initMap();
        }
        else
        {
            Toast.makeText(this, "Failed to initmap!!!", Toast.LENGTH_SHORT).show();
        }

        listSP = new ArrayList<>();
        //Set custom action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_create_stop_action_bar);
        actionBarView = actionBar.getCustomView();

        //Set Confirm exit dialog
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setMessage("All stop points that were set will be discarded.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
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

        //Set search view for place
        final SearchView searchView = actionBarView.findViewById(R.id.search_location);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                searchView.clearFocus();
            }
        }, 300);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                geoLocate(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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
            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    setMarker(latLng.latitude, latLng.longitude);
                    Toast.makeText(CreateStopsActivity.this, getCompleteAddressString(latLng.latitude, latLng.longitude), Toast.LENGTH_LONG).show();
                }
            });
            mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    AddStopPointDialog addStopPointDialog = new AddStopPointDialog();
                    Bundle bundle = new Bundle();
                    LatLng latLng = marker.getPosition();
                    bundle.putString("Address", getCompleteAddressString(latLng.latitude, latLng.longitude));
                    bundle.putDouble("Latitude", latLng.latitude);
                    bundle.putDouble("Longitude", latLng.longitude);
                    addStopPointDialog.setArguments(bundle);
                    addStopPointDialog.show(getSupportFragmentManager(), "Add stop point dialog");
                    return false;
                }
            });
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
                    Toast.makeText(getApplicationContext(), getCompleteAddressString(ll.latitude, ll.longitude), Toast.LENGTH_SHORT).show();

                }
            });
        }

        goToLocationZoom(10.763182, 106.682494, 12);

        mGoogleMap.setMyLocationEnabled(true);
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                                .addApi(LocationServices.API)
//                                .addConnectionCallbacks(this)
//                                .addOnConnectionFailedListener(this)
//                                .build();
//        mGoogleApiClient.connect();

//
//        FloatingActionButton mCurrentLocation = findViewById(R.id.my_cr_location);
//        mCurrentLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LatLng latLng = new LatLng(Double.parseDouble(getLatitude()), Double.parseDouble(getLongitude()));
//                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 18);
//                mGoogleMap.animateCamera(cameraUpdate);
//            }
//        });

    }

    private void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mGoogleMap.animateCamera(cameraUpdate);
    }
    Marker marker;
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
            Toast.makeText(this, getCompleteAddressString(lat, lng), Toast.LENGTH_LONG).show();
            goToLocationZoom(lat, lng,16);

            setMarker(lat, lng);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMarker(double lat, double lng) {
        if (marker != null){
            marker.remove();
        }
        MarkerOptions options = new MarkerOptions().draggable(true).position(new LatLng(lat, lng));
        marker = mGoogleMap.addMarker(options);
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
        if (location == null){
            Toast.makeText(this, "Can't get current location", Toast.LENGTH_LONG).show();
        }
        else
        {
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, 13);
            mGoogleMap.animateCamera(update);
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

    @Override
    public void applyData(StopPointInfo stopPointInfo) {
        listSP.add(stopPointInfo);
        Log.d("LIST SP -----> ", listSP.toString());
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
                Log.w("loction address", strReturnedAddress.toString());
            } else {
                Log.w("loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("loction address", "Canont get Address!");
        }
        return strAdd;
    }
}