package com.imeja.carpooling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imeja.carpooling.auth.documents.DocumentsActivity;
import com.imeja.carpooling.chat.ChatActivity;
import com.imeja.carpooling.coolmap.MapsActivity;
import com.imeja.carpooling.help.HelpActivity;
import com.imeja.carpooling.map.PlaceFragment;
import com.imeja.carpooling.model.Account;
import com.imeja.carpooling.model.RealmUtils;
import com.imeja.carpooling.myaccount.MyAccountActivity;
import com.imeja.carpooling.net.Endpoints;
import com.imeja.carpooling.notification.NotificationActivity;
import com.imeja.carpooling.offers.OffersActivity;
import com.imeja.carpooling.payments.PaymentsActivity;
import com.imeja.carpooling.preferences.PreferenceManager;
import com.imeja.carpooling.rewards.RewardsActivity;
import com.imeja.carpooling.riders.RidesActivity;
import com.imeja.carpooling.searching.SearchActivity;
import com.imeja.carpooling.settings.SettingsActivity;
import com.imeja.carpooling.trustmanager.HttpsTrustManager;
import com.imeja.carpooling.vehicle.VehiclesActivity;
import com.imeja.carpooling.wallet.WalletActivity;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.RealmChangeListener;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMapClickListener, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, NavigationView.OnNavigationItemSelectedListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLANNING_TO_GO = 10;
    private static final int PICKING_POINT = 11;
    private static final int RIDER_DROP_POINT = 12;
    private static final int RIDER_PICKING_POINT = 13;
    public DrawerLayout drawerLayout;

    private Activity mActivity;
    private Context mContext;
    AlertDialog alertDialog;
    PlaceFragment placesFragment;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private MarkerOptions markerOptions;
    private PlaceDetectionClient placeDetectionClient;
    private GeoDataClient geoDataClient;
    private String location = "";
    private FusedLocationProviderClient mFusedLocationClient;
    private Marker marker;
    private boolean added;
    private LocationRequest locationRequest;
    Location mLastLocation;
    public static LatLng NAIROBI = new LatLng(-1.28333, 36.81667);
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private final String PLACES = "places";
    private FirebaseAuth mAuth;
    LinearLayout offeringRide, findingRide;

    EditText planning_to_go, which_date, seat_to_book, where_to_pick_you;
    DatePickerDialog datePickerDialog;
    ProgressDialog pDialog;
    String going, siku, viti, hapa;
    String phone;

    EditText riderGoing, rideDate, riderSeats, riderPick;
    String rider_Going, ride_Date, rider_Seats, rider_Pick;
    private LatLng latLng;

    CircleImageView profile;
    TextView name;
    Account account;
    PreferenceManager preferenceManager;
    Button find, offer;
    NavigationView navigationView;

    Toolbar toolbar;
    String price, ride_on, latitude_to, longitude_to, address_to, latitude_from, longitude_from, address_from, user_id, provider_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.mDrawerLayout);
        offeringRide = findViewById(R.id.offeringRide);
        findingRide = findViewById(R.id.findingRide);
        preferenceManager = new PreferenceManager(MainActivity.this);
        navigationView = findViewById(R.id.main_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        /*driver*/
        riderGoing = findViewById(R.id.riderGoing);
        rideDate = findViewById(R.id.rideDate);
        riderSeats = findViewById(R.id.riderSeats);
        riderPick = findViewById(R.id.riderPick);

        offer = findViewById(R.id.offer);
        find = findViewById(R.id.find);
        riderGoing.setKeyListener(null);
        riderGoing.setCursorVisible(false);

        rideDate.setKeyListener(null);
        rideDate.setCursorVisible(false);

        riderSeats.setKeyListener(null);
        riderSeats.setCursorVisible(false);

        riderPick.setKeyListener(null);
        riderPick.setCursorVisible(false);

        planning_to_go = findViewById(R.id.planning_to_go);
        which_date = findViewById(R.id.which_date);
        seat_to_book = findViewById(R.id.seat_to_book);
        where_to_pick_you = findViewById(R.id.where_to_pick_you);

        planning_to_go.setKeyListener(null);
        planning_to_go.setCursorVisible(false);

        which_date.setKeyListener(null);
        which_date.setCursorVisible(false);

        seat_to_book.setKeyListener(null);
        seat_to_book.setCursorVisible(false);

        where_to_pick_you.setKeyListener(null);
        where_to_pick_you.setCursorVisible(false);

        // Initialize Firebase Auth
        offeringRide.setVisibility(View.GONE);
        findingRide.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();

        /*Maps*/
        placesFragment = new PlaceFragment();
        geoDataClient = Places.getGeoDataClient(this);
        placeDetectionClient = Places.getPlaceDetectionClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        loadUserAccount();
        account = RealmUtils.getAccount();
        account.addChangeListener(new RealmChangeListener<Account>() {
            @Override
            public void onChange(@NonNull Account person) {
                // React to change
                account = person;
                setData();
            }
        });
        setData();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 second delay between each request
        locationRequest.setFastestInterval(5000); // 5 seconds fastest time in between each request
        locationRequest.setSmallestDisplacement(10); // 10 meters minimum displacement for new location request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // enables GPS high accuracy location requests

        getUpdatedLocation();

        planning_to_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openMap(PLANNING_TO_GO);
            }
        });
        where_to_pick_you.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openMap(PICKING_POINT);
            }
        });
        seat_to_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectSeats();
            }
        });
        riderSeats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRiderSeats();
            }
        });
        which_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectDate();
            }
        });
        rideDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectriderDate();

            }
        });
        riderGoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap(RIDER_DROP_POINT);

            }
        });
        riderPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap(RIDER_PICKING_POINT);

            }
        });

        /*Rider*/


    }


    private void openProfile() {
        startActivity(new Intent(MainActivity.this, MyAccountActivity.class));
    }

    private void setData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PLANNING_TO_GO) {
            latLng = data.getParcelableExtra(MapsActivity.LATLNG);
            String locationName = data.getStringExtra(MapsActivity.PLACE_NAME);
            planning_to_go.setText("" + locationName);

            longitude_to= String.valueOf(latLng.longitude);
            latitude_to = String.valueOf(latLng.latitude);
            address_to=locationName;

        } else if (resultCode == RESULT_OK && requestCode == PICKING_POINT) {
            latLng = data.getParcelableExtra(MapsActivity.LATLNG);
            String locationName = data.getStringExtra(MapsActivity.PLACE_NAME);
            where_to_pick_you.setText("" + locationName);

            longitude_from= String.valueOf(latLng.longitude);
            latitude_from = String.valueOf(latLng.latitude);
            address_from=locationName;

        } else if (resultCode == RESULT_OK && requestCode == RIDER_DROP_POINT) {
            latLng = data.getParcelableExtra(MapsActivity.LATLNG);
            String locationName = data.getStringExtra(MapsActivity.PLACE_NAME);
            riderGoing.setText("" + locationName);

            longitude_to= String.valueOf(latLng.longitude);
            latitude_to = String.valueOf(latLng.latitude);
            address_to=locationName;

        } else if (resultCode == RESULT_OK && requestCode == RIDER_PICKING_POINT) {
            latLng = data.getParcelableExtra(MapsActivity.LATLNG);
            String locationName = data.getStringExtra(MapsActivity.PLACE_NAME);
            riderPick.setText("" + locationName);

            longitude_from= String.valueOf(latLng.longitude);
            latitude_from = String.valueOf(latLng.latitude);
            address_from=locationName;
        }
    }

    private void openMap(int planningToGo) {
        Log.e(TAG, "onClick: opening map------");
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        if (latLng != null) {
            intent.putExtra(MapsActivity.LATLNG, latLng);
        }
        startActivityForResult(intent, planningToGo);
    }


    private void loadUserAccount() {

        final String phone = RealmUtils.getPhoneNumber();
        if (phone != null) {
            Log.d("User Account", "Account=>" + phone);
            HttpsTrustManager.allowAllSSL();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.ACCOUNT_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("MyAccount ->", response);
                            if (response.contains(getString(R.string.pdoexception))) {

                            } else if (response.isEmpty() || response.equalsIgnoreCase("[]")) {

                            } else {

                                Type listType = new TypeToken<List<Account>>() {
                                }.getType();
                                List<Account> accounts = new Gson().fromJson(response, listType);
                                Account account = accounts.get(0);
                                RealmUtils.updateAccount(account);

                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want

                        }
                    }) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("phone", phone);

                    //returning parameter.
                    return params;
                }
            };

            //Adding the string request to the queue
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);
            stringRequest.setShouldCache(false);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
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

    private void addPlaceFragment() {
        if (!added) {
            added = true;
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, placesFragment, PLACES)
                    .addToBackStack(PLACES)
                    .commit();
        }
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

    public void newLocation(AutocompletePrediction prediction) {
        geoDataClient.getPlaceById(prediction.getPlaceId()).addOnSuccessListener(new OnSuccessListener<PlaceBufferResponse>() {
            public void onSuccess(PlaceBufferResponse p0) {
                Log.e(TAG, "total places count is " + p0.getCount());
                if (p0.getCount() > 0) {
                    Place place = p0.get(0);
                    NAIROBI = place.getLatLng();
                    location = place.getName().toString();
                    //Log.e(TAG, "onSuccess: place is " + new Gson().toJson(place));
                    setMarker();
                }
            }

        });
        removePlacesFragment(null);
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

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

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
        setMarker();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.notification) {
            startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_manu, menu);
        return true;
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

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        NAIROBI = latLng;
        setMarker();

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

    @Override
    public void onClick(View view) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    public void handleFindRide(View view) {
        offeringRide.setVisibility(View.GONE);
        findingRide.setVisibility(View.VISIBLE);
        find.setBackgroundResource(R.drawable.freebut);
        offer.setBackground(null);
    }

    public void handleOfferRide(View view) {
        String licence = RealmUtils.getLicence();
        offer.setBackgroundResource(R.drawable.freebut);
        find.setBackground(null);
        if (licence != null) {
            offeringRide.setVisibility(View.VISIBLE);
            findingRide.setVisibility(View.GONE);
        } else {
            Intent intent = new Intent(MainActivity.this, DocumentsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("documents", "documents");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void handleFindPost(View view) {
        going = planning_to_go.getText().toString().trim();
        siku = which_date.getText().toString().trim();
        viti = seat_to_book.getText().toString().trim();
        hapa = where_to_pick_you.getText().toString().trim();
        if (going.isEmpty() || siku.isEmpty() || viti.isEmpty() || hapa.isEmpty()) {

            Snackbar.make(drawerLayout, "Check all fields", Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_refresh, null).show();
        } else {
            preferenceManager.setGoing(going, siku, viti, hapa);
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);

        }
    }

    private void selectDate() {
        GregorianCalendar gc = new GregorianCalendar();
        int day, month, year;
        day = gc.get(Calendar.DAY_OF_MONTH);
        month = gc.get(Calendar.MONTH);
        year = gc.get(Calendar.YEAR);
        datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                which_date.setText("" + dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.getWindow().getAttributes().windowAnimations = R.style.LeftRightDialogTheme;
        datePickerDialog.show();
    }

    private void selectSeats() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.find_ride_seats);
        final Button one = dialog.findViewById(R.id.one);
        final Button two = dialog.findViewById(R.id.two);
        final Button three = dialog.findViewById(R.id.three);
        final Button four = dialog.findViewById(R.id.four);
        final Button five = dialog.findViewById(R.id.five);
        Button six = dialog.findViewById(R.id.six);
        dialog.setTitle("Find Ride Seat Selection");

        final TextView select_info = dialog.findViewById(R.id.select_info);


        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_info.setText("1");

                one.setBackgroundResource(R.drawable.button_drawable);
                two.setBackgroundResource(R.drawable.button_drawable_blank);
                three.setBackgroundResource(R.drawable.button_drawable_blank);
                four.setBackgroundResource(R.drawable.button_drawable_blank);
                five.setBackgroundResource(R.drawable.button_drawable_blank);
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                select_info.setText("2");

                one.setBackgroundResource(R.drawable.button_drawable_blank);
                two.setBackgroundResource(R.drawable.button_drawable);
                three.setBackgroundResource(R.drawable.button_drawable_blank);
                four.setBackgroundResource(R.drawable.button_drawable_blank);
                five.setBackgroundResource(R.drawable.button_drawable_blank);
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_info.setText("3");

                one.setBackgroundResource(R.drawable.button_drawable_blank);
                two.setBackgroundResource(R.drawable.button_drawable_blank);
                three.setBackgroundResource(R.drawable.button_drawable);
                four.setBackgroundResource(R.drawable.button_drawable_blank);
                five.setBackgroundResource(R.drawable.button_drawable_blank);
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_info.setText("4");

                one.setBackgroundResource(R.drawable.button_drawable_blank);
                two.setBackgroundResource(R.drawable.button_drawable_blank);
                three.setBackgroundResource(R.drawable.button_drawable_blank);
                four.setBackgroundResource(R.drawable.button_drawable);
                five.setBackgroundResource(R.drawable.button_drawable_blank);
            }
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_info.setText("5");

                one.setBackgroundResource(R.drawable.button_drawable_blank);
                two.setBackgroundResource(R.drawable.button_drawable_blank);
                three.setBackgroundResource(R.drawable.button_drawable_blank);
                four.setBackgroundResource(R.drawable.button_drawable_blank);
                five.setBackgroundResource(R.drawable.button_drawable);
            }
        });

        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (select_info.getText().toString().isEmpty()) {

                    Toast.makeText(MainActivity.this, "Select seat number", Toast.LENGTH_SHORT).show();
                } else {
                    seat_to_book.setText(select_info.getText().toString());
                    dialog.dismiss();

                }
            }
        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.LeftRightDialogTheme;
        dialog.show();
    }

    private void selectriderDate() {

        GregorianCalendar gc = new GregorianCalendar();
        int day, month, year;
        day = gc.get(Calendar.DAY_OF_MONTH);
        month = gc.get(Calendar.MONTH);
        year = gc.get(Calendar.YEAR);
        datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                rideDate.setText("" + dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.getWindow().getAttributes().windowAnimations = R.style.LeftRightDialogTheme;
        datePickerDialog.show();
    }

    private void selectRiderSeats() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.offer_ride_seats);
        final Button one = dialog.findViewById(R.id.one);
        final Button two = dialog.findViewById(R.id.two);
        final Button three = dialog.findViewById(R.id.three);
        final Button four = dialog.findViewById(R.id.four);
        final Button five = dialog.findViewById(R.id.five);
        Button six = dialog.findViewById(R.id.six);
        dialog.setTitle("Offer Ride Seat Selection");

        final TextView select_info = dialog.findViewById(R.id.select_info);


        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_info.setText("1");
                one.setBackgroundResource(R.drawable.button_drawable);
                two.setBackgroundResource(R.drawable.button_drawable_blank);
                three.setBackgroundResource(R.drawable.button_drawable_blank);
                four.setBackgroundResource(R.drawable.button_drawable_blank);
                five.setBackgroundResource(R.drawable.button_drawable_blank);
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                select_info.setText("2");

                one.setBackgroundResource(R.drawable.button_drawable_blank);
                two.setBackgroundResource(R.drawable.button_drawable);
                three.setBackgroundResource(R.drawable.button_drawable_blank);
                four.setBackgroundResource(R.drawable.button_drawable_blank);
                five.setBackgroundResource(R.drawable.button_drawable_blank);
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_info.setText("3");

                one.setBackgroundResource(R.drawable.button_drawable_blank);
                two.setBackgroundResource(R.drawable.button_drawable_blank);
                three.setBackgroundResource(R.drawable.button_drawable);
                four.setBackgroundResource(R.drawable.button_drawable_blank);
                five.setBackgroundResource(R.drawable.button_drawable_blank);
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_info.setText("4");

                one.setBackgroundResource(R.drawable.button_drawable_blank);
                two.setBackgroundResource(R.drawable.button_drawable_blank);
                three.setBackgroundResource(R.drawable.button_drawable_blank);
                four.setBackgroundResource(R.drawable.button_drawable);
                five.setBackgroundResource(R.drawable.button_drawable_blank);
            }
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_info.setText("5");

                one.setBackgroundResource(R.drawable.button_drawable_blank);
                two.setBackgroundResource(R.drawable.button_drawable_blank);
                three.setBackgroundResource(R.drawable.button_drawable_blank);
                four.setBackgroundResource(R.drawable.button_drawable_blank);
                five.setBackgroundResource(R.drawable.button_drawable);
            }
        });

        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (select_info.getText().toString().isEmpty()) {

                    Toast.makeText(MainActivity.this, "Select seat number", Toast.LENGTH_SHORT).show();
                } else {
                    riderSeats.setText(select_info.getText().toString());
                    dialog.dismiss();
                    //startActivity(new Intent(MainActivity.this, RiderProfileActivity.class));

                }
            }
        });
        dialog.getWindow().getAttributes().windowAnimations = R.style.LeftRightDialogTheme;
        dialog.show();
    }

    public void handleOfferPost(View view) {

        price = "100";
        ride_on = rideDate.getText().toString().trim();


        user_id = RealmUtils.getPhoneNumber();
        provider_id = RealmUtils.getPhoneNumber();

        rider_Going = riderGoing.getText().toString().trim();
        ride_Date = rideDate.getText().toString().trim();
        rider_Seats = riderSeats.getText().toString().trim();
        rider_Pick = riderPick.getText().toString().trim();

        if (ride_Date.isEmpty() || rider_Seats.isEmpty()) {
            Snackbar.make(drawerLayout, "Check all fields", Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_refresh, null).show();
        } else {
            showDialog();
            HttpsTrustManager.allowAllSSL();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.OFFER_RIDE_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("MyAccount ->", response);
                            dismissDialog();
                            if (response.contains(getString(R.string.pdoexception))) {
                                Snackbar.make(drawerLayout, R.string.error_occured, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.btn_refresh, null).show();
                            } else {
                                if (response.equalsIgnoreCase("Success")) {

                                } else {
                                    Snackbar.make(drawerLayout, response, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.btn_refresh, null).show();
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            dismissDialog();
                            Snackbar.make(drawerLayout, R.string.error_occured, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.btn_refresh, null).show();
                        }
                    }) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("provider_id", RealmUtils.getPhoneNumber());
                    params.put("user_id", RealmUtils.getPhoneNumber());

                    params.put("address_to", address_to);
                    params.put("longitude_to", longitude_to);
                    params.put("latitude_to", latitude_to);

                    params.put("address_from", address_from);
                    params.put("latitude_from", latitude_from);
                    params.put("longitude_from", longitude_from);

                    params.put("seat", rider_Seats);

                    params.put("ride_on", ride_on);
                    params.put("price", price);

                    //returning parameter.
                    return params;
                }
            };

            //Adding the string request to the queue
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);
            stringRequest.setShouldCache(false);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
    }

    public void showDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Creating a Ride\nPlease Wait...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.getWindow().getAttributes().windowAnimations = R.style.LeftRightDialogTheme;
        pDialog.show();
    }

    public void dismissDialog() {
        pDialog.dismiss();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_rides) {
            startActivity(new Intent(MainActivity.this, RidesActivity.class));
        }
        if (id == R.id.action_pay) {
            startActivity(new Intent(MainActivity.this, PaymentsActivity.class));
        }
        if (id == R.id.action_vehicle) {
            startActivity(new Intent(MainActivity.this, VehiclesActivity.class));
        }
        if (id == R.id.action_contribute) {
            startActivity(new Intent(MainActivity.this, WalletActivity.class));
        }
        if (id == R.id.action_chathere) {
            startActivity(new Intent(MainActivity.this, ChatActivity.class));
        }
        if (id == R.id.action_offer) {
            startActivity(new Intent(MainActivity.this, OffersActivity.class));
        }
        if (id == R.id.action_share) {

        }
        if (id == R.id.action_refer) {
            startActivity(new Intent(MainActivity.this, RewardsActivity.class));
        }
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }
        if (id == R.id.action_help_mee) {
            startActivity(new Intent(MainActivity.this, HelpActivity.class));
        }
        if (id == R.id.action_logout) {
            alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(getString(R.string.app_name));
            alertDialog.setMessage("Are you sure you want to Log Out?");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            RealmUtils.setLogged(false);
                            finish();
                        }
                    });

            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    });
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.LeftRightDialogTheme;
            alertDialog.show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
