package com.imeja.carpooling.auth.selection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.imeja.carpooling.R;
import com.imeja.carpooling.auth.documents.DocumentsActivity;
import com.imeja.carpooling.auth.name.NamesActivity;
import com.imeja.carpooling.auth.profile.ProfileActivity;
import com.imeja.carpooling.model.RealmUtils;
import com.imeja.carpooling.net.Endpoints;
import com.imeja.carpooling.trustmanager.HttpsTrustManager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class SelectionActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    LinearLayout section_one, section_two;
    CoordinatorLayout coordinatorLayout;
    FloatingActionButton floatingActionButton;
    boolean completed = false;

    ProgressDialog pDialog;
    String phone;
    EditText edthome, edtstart, edtoffice, edtleave;
    String home, start, office, leave;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            phone = currentUser.getPhoneNumber();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        getSupportActionBar().setTitle("Route Details");
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        /*EditText*/
        edthome = findViewById(R.id.edthome);
        edtstart = findViewById(R.id.edtstart);
        edtoffice = findViewById(R.id.edtoffice);
        edtleave = findViewById(R.id.edtleave);

        section_one = findViewById(R.id.section_one);
        section_two = findViewById(R.id.section_two);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        section_two.setVisibility(View.GONE);
        floatingActionButton = findViewById(R.id.next);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFinder();
            }
        });
        edtleave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLeave();
            }
        });
        edtstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleStart();
            }
        });
    }

    private void handleStart() {

        GregorianCalendar gc = new GregorianCalendar();
        int min, hr;
        min = gc.get(Calendar.MINUTE);
        hr = gc.get(Calendar.HOUR_OF_DAY);

        TimePickerDialog timePickerDialog = new TimePickerDialog(SelectionActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                edtstart.setText(i + ":" + i1 + "");
            }
        }, hr, min, true);

        timePickerDialog.show();
    }

    private void handleLeave() {
        GregorianCalendar gc = new GregorianCalendar();
        int min, hr;
        min = gc.get(Calendar.MINUTE);
        hr = gc.get(Calendar.HOUR_OF_DAY);

        TimePickerDialog timePickerDialog = new TimePickerDialog(SelectionActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                edtleave.setText(i + ":" + i1 + "");
            }
        }, hr, min, true);
        timePickerDialog.show();

    }

    public void handleFinder() {

        home = edthome.getText().toString().trim();
        start = edtstart.getText().toString().trim();
        office = edtoffice.getText().toString().trim();
        leave = edtleave.getText().toString().trim();

        if (home.isEmpty()) {
            edthome.setError("Enter Home Address");
            edthome.requestFocus();

        } else if (start.isEmpty()) {
            edtstart.setError("Enter your starting time");
            edtstart.requestFocus();
        } else if (office.isEmpty()) {
            edtoffice.setError("Enter Office Address");
            edtoffice.requestFocus();
        } else if (leave.isEmpty()) {
            edtleave.setError("Enter your leaving time");
            edtleave.requestFocus();
        } else {
            proceedWithFinding(home, start, office, leave);
        }


    }

    private void proceedWithFinding(final String home, final String start, final String office, final String leave) {

        showDialog();
        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.FINDER_DETAILS_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        DismissDialog();
                        if (response.equalsIgnoreCase("Success! Record saved successfully!")) {

                            startActivity(new Intent(SelectionActivity.this, ProfileActivity.class));
                            SelectionActivity.this.finish();
                        } else {
                            if (response.contains("PDOException")) {
                                Snackbar.make(coordinatorLayout, "Error encountered", Snackbar.LENGTH_LONG)
                                        .setAction(R.string.btn_refresh, null).show();

                            } else {
                                Snackbar.make(coordinatorLayout, response, Snackbar.LENGTH_LONG)
                                        .setAction(R.string.btn_refresh, null).show();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DismissDialog();

                        Snackbar.make(coordinatorLayout, R.string.error_occured, Snackbar.LENGTH_LONG)
                                .setAction(R.string.btn_refresh, null).show();
                    }

                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request

                params.put("phone", RealmUtils.getPhoneNumber());
                params.put("home", home);
                params.put("start", start);
                params.put("office", office);
                params.put("leave", leave);

                //returning parameter.
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    public void handleOffer(View view) {

        final Dialog dialog = new Dialog(SelectionActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.offer_ride);
        dialog.setTitle("Offer a Ride");

        final Spinner car_type = dialog.findViewById(R.id.car_type);
        final EditText car_number = dialog.findViewById(R.id.car_number);
        Button submit = dialog.findViewById(R.id.submit);
        Button one = dialog.findViewById(R.id.one);
        Button two = dialog.findViewById(R.id.two);
        Button three = dialog.findViewById(R.id.three);
        Button four = dialog.findViewById(R.id.four);
        Button five = dialog.findViewById(R.id.five);
        dialog.setTitle("Find Ride Seat Selection");

        final TextView select_info = dialog.findViewById(R.id.select_info);


        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_info.setText("1");
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                select_info.setText("2");
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_info.setText("3");
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_info.setText("4");
            }
        });

        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                select_info.setText("5");
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String car = car_type.getSelectedItem().toString().trim();
                String no = car_number.getText().toString().trim();
                String seat = select_info.getText().toString().trim();
                if (car.isEmpty() || no.isEmpty() || seat.isEmpty()) {
                    Snackbar.make(coordinatorLayout, R.string.check_all_fields, Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_refresh, null).show();
                } else {
                    saveOffer(car, no, seat);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    private void saveOffer(final String car, final String no, final String seat) {
        showDialog();
        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.OFFER_DETAILS_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        DismissDialog();
                        if (response.equalsIgnoreCase("Success! Record saved successfully!")) {

                            startActivity(new Intent(SelectionActivity.this, DocumentsActivity.class));
                            SelectionActivity.this.finish();
                        } else {
                            if (response.contains("PDOException")) {

                            } else {
                                Toast.makeText(SelectionActivity.this, response, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DismissDialog();

                        Snackbar.make(coordinatorLayout, R.string.error_occured, Snackbar.LENGTH_LONG)
                                .setAction(R.string.btn_refresh, null).show();
                    }

                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request

                params.put("phone", RealmUtils.getPhoneNumber());
                params.put("car", car);
                params.put("no", no);
                params.put("seat", seat);

                //returning parameter.
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void showDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setMessage("Please Wait...");
        pDialog.show();

    }

    public void DismissDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    public void handleFind(View view) {
        section_two.setVisibility(View.VISIBLE);
        section_one.setVisibility(View.GONE);
    }
}
