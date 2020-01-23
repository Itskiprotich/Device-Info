package com.imeja.carpooling.auth.name;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.imeja.carpooling.auth.profile.ProfileActivity;
import com.imeja.carpooling.auth.selection.SelectionActivity;
import com.imeja.carpooling.model.RealmUtils;
import com.imeja.carpooling.net.Endpoints;
import com.imeja.carpooling.trustmanager.HttpsTrustManager;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamesActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FloatingActionButton floatingActionButton;
    ProgressDialog pDialog;
    String phone = null;
    EditText fname, lname, email, pass, passtwo;
    String stfname, stlname, stemail, stpass, stpasstwo;

    CoordinatorLayout coordinatorLayout;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
/*
        if (currentUser != null) ;
        phone = currentUser.getPhoneNumber();*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_names);
        getSupportActionBar().setTitle("Personal Details");
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        /*EditText*/
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        email = findViewById(R.id.email);
        pass = findViewById(R.id.pass);
        passtwo = findViewById(R.id.passtwo);


        floatingActionButton = findViewById(R.id.next);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkInput();
            }
        });
    }

    private void checkInput() {
        stfname = fname.getText().toString().trim();
        stlname = lname.getText().toString().trim();
        stemail = email.getText().toString().trim();
        stpass = pass.getText().toString().trim();
        stpasstwo = passtwo.getText().toString().trim();

        if (stfname.isEmpty()) {
            fname.setError("Enter First Name");
            fname.requestFocus();
            return;
        } else if (stlname.isEmpty()) {
            lname.setError("Enter Last Name");
            lname.requestFocus();
            return;
        } else if (stemail.isEmpty()) {
            email.setError("Enter Email Address");
            email.requestFocus();
            return;
        } else if (!validEmail(stemail)) {
            email.setError("Enter Valid Email Address");
            email.requestFocus();
            return;
        } else if (stpass.isEmpty()) {
            pass.setError("Enter Password");
            pass.requestFocus();
            return;
        } else if (stpasstwo.isEmpty()) {
            passtwo.setError("Enter Confirm Password");
            passtwo.requestFocus();
            return;
        } else {
            if (stpass.equalsIgnoreCase(stpasstwo)) {

                showDialog();
                HttpsTrustManager.allowAllSSL();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.PERSONAL_DETAILS_URL,
                        new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                DismissDialog();
                                if (response.equalsIgnoreCase("Success! Record saved successfully!")) {

                                    startActivity(new Intent(NamesActivity.this, ProfileActivity.class));
                                    NamesActivity.this.finish();
                                } else {
                                    if (response.contains("PDOException")) {

                                    } else {
                                        Toast.makeText(NamesActivity.this, response, Toast.LENGTH_SHORT).show();
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
                        params.put("fname", stfname);
                        params.put("lname", stlname);
                        params.put("email", stemail);
                        params.put("pass", stpass);

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

            } else {
                Snackbar.make(coordinatorLayout, R.string.password_not_match, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_refresh, null).show();

            }
        }
    }

    private boolean validEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
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
}
