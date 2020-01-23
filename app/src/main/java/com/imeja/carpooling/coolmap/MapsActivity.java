package com.imeja.carpooling.coolmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.imeja.carpooling.R;
import com.imeja.carpooling.map.PlaceFragment;
import com.imeja.carpooling.map.PlaceSearchFragment;
import com.imeja.carpooling.model.AppUtils;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMapClickListener, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String LATLNG = "LAT";
    public static final String PLACE_NAME = "name";
    public static final int RQ = 1000;
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = MapsActivity.class.getSimpleName();
    public static LatLng NAIROBI = new LatLng(-1.28333, 36.81667);
    private final String PLACES = "places";
    public PlaceSearchFragment placesFragment;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient; // Object used to receive location updates

    private LocationRequest locationRequest; // Object that defines important parameters regarding location request.

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private MarkerOptions markerOptions;
    private EditText editText;
    private PlaceDetectionClient placeDetectionClient;
    private GeoDataClient geoDataClient;
    private String location = "";
    private Button okBtn, cancelBtn;
    private Marker marker;
    private boolean added;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(null);
        LatLng latLng = getIntent().getParcelableExtra(LATLNG);
        if (latLng != null) {
            NAIROBI = latLng;
        }
        editText = findViewById(R.id.search_view);
        okBtn = findViewById(R.id.ok_button);
        cancelBtn = findViewById(R.id.cancel_button);
        placesFragment = new PlaceSearchFragment();
        geoDataClient = Places.getGeoDataClient(this);
        placeDetectionClient = Places.getPlaceDetectionClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 second delay between each request
        locationRequest.setFastestInterval(5000); // 5 seconds fastest time in between each request
        locationRequest.setSmallestDisplacement(10); // 10 meters minimum displacement for new location request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // enables GPS high accuracy location requests

        getUpdatedLocation();

    }

    private void getUpdatedLocation() {

        try {
            mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {

                    mLastLocation = locationResult.getLastLocation();
                }
            }, Looper.myLooper());

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            } else {
                enableMyLocation();
                //Request Location Permission

            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        UiSettings settings = mMap.getUiSettings();
        settings.setAllGesturesEnabled(true);
        settings.setCompassEnabled(true);
        settings.setScrollGesturesEnabled(true);
        settings.setZoomControlsEnabled(true);
        settings.setZoomGesturesEnabled(true);
        // Add a marker in Sydney and move the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NAIROBI, 15));
        mMap.setOnMyLocationClickListener(this);
        mMap.setOnMapClickListener(this);

        enableMyLocation();
        // setMarker();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();
                if (!newText.isEmpty()) {
                    searchLocation(newText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
               /* String newText = s.toString();
                if (!newText.isEmpty()) {
                    searchLocation(newText);
                }*/
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void setMarker() {
        if (marker != null) {
            marker.remove();
        }
        markerOptions = new MarkerOptions()
                .position(NAIROBI)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        marker = mMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(NAIROBI);
        mMap.animateCamera(cameraUpdate);
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            AppUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (AppUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        AppUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onMapClick(LatLng latLng) {
        NAIROBI = latLng;
        setMarker();
    }

    private void addPlaceFragment() {
        if (!added) {
            added = true;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, placesFragment, PLACES)
                    .addToBackStack(PLACES)
                    .commit();
        }
    }

    public void newLocation(AutocompletePrediction prediction) {
        geoDataClient.getPlaceById(prediction.getPlaceId()).addOnSuccessListener(new OnSuccessListener<PlaceBufferResponse>() {
            public void onSuccess(PlaceBufferResponse p0) {
                Log.e(TAG, "total places count is " + p0.getCount());
                if (p0.getCount() > 0) {
                    Place place = p0.get(0);
                    NAIROBI = place.getLatLng();
                    location = place.getName().toString();
                    Log.e(TAG, "onSuccess: place is " + new Gson().toJson(place));
                    setMarker();
                }
            }

        });
        removePlacesFragment(null);
    }

    private void searchLocation(String name) {
        addPlaceFragment();
        geoDataClient.getAutocompletePredictions(name, null, null)
                .addOnSuccessListener(new OnSuccessListener<AutocompletePredictionBufferResponse>() {
                    @Override
                    public void onSuccess(AutocompletePredictionBufferResponse autocompletePredictions) {
                        if (autocompletePredictions.getCount() == 0) {
                            placesFragment.setPredictions(new ArrayList<AutocompletePrediction>());
                        } else {
                            ArrayList<AutocompletePrediction> predictions = new ArrayList<AutocompletePrediction>();
                            for (AutocompletePrediction prediction : autocompletePredictions) {
                                predictions.add(prediction);
                            }
                            placesFragment.setPredictions(predictions);
                        }

                    }
                });
    }

    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PLACES);
        if (fragment != null) {
            removePlacesFragment(fragment);
        } else {
            super.onBackPressed();
        }
    }

    private void removePlacesFragment(Fragment fragment) {
        if (fragment == null) {
            fragment = getSupportFragmentManager().findFragmentByTag(PLACES);
        }
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
            added = false;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_button:
                getIntent().putExtra(LATLNG, NAIROBI)
                        .putExtra(PLACE_NAME, location);
                setResult(RESULT_OK, getIntent());
                break;

            case R.id.cancel_button:
                setResult(RESULT_CANCELED);
                break;
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop location updates if activity is no longer active
        if (mGoogleApiClient != null) {
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
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
        mLastLocation = location;
        //Place current location marker
        NAIROBI = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        setMarker();
    }


}
