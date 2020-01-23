package com.imeja.carpooling.riderprofile.frags;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imeja.carpooling.R;
import com.imeja.carpooling.model.Reviews;
import com.imeja.carpooling.net.Endpoints;
import com.imeja.carpooling.riderprofile.adapters.ReviewsAdapter;
import com.imeja.carpooling.trustmanager.HttpsTrustManager;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class RiderReviewsFragment extends Fragment {

    private static final String TAG = RiderReviewsFragment.class.getSimpleName();
    LinearLayout empty;
    ProgressBar loadingProgressBar;
    RecyclerView recyclerView;
    Button loadmoreButton;
    RelativeLayout coordinatorLayout;
    String phone;
    private FirebaseAuth mAuth;
    private ArrayList<Reviews> riders;
    ReviewsAdapter reviewsAdapter;
    private int nextPosition = -1;

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
    public RiderReviewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_rider_reviews, container, false);
        mAuth = FirebaseAuth.getInstance();
        empty = view.findViewById(R.id.empty);
        riders = new ArrayList<>();
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        loadmoreButton = view.findViewById(R.id.loadmoreButton);
        empty.setVisibility(View.GONE);
        loadmoreButton.setVisibility(View.GONE);
        coordinatorLayout=view.findViewById(R.id.coordinatorLayout);
        //loadReviews();
        loadmoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMore();
            }
        });
        return view;
    }

    private void loadMore() {
        loadmoreButton.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);
        if (nextPosition != riders.size() - 1) {
            final List<Reviews> sub = riders.subList(nextPosition, riders.size());
            if (sub.size() > 15) {
                nextPosition += 15;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reviewsAdapter.addItems(sub.subList(0, 14));
                        loadmoreButton.setVisibility(View.VISIBLE);
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                }, 2000);

            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reviewsAdapter.addItems(sub);
                        loadmoreButton.setVisibility(View.GONE);
                        loadingProgressBar.setVisibility(View.GONE);
                    }
                }, 2000);

            }
            Log.e(TAG, "new position is " + nextPosition);
        }
    }

    private void loadReviews() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Endpoints.REVIEW_DETAILS_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        loadingProgressBar.setVisibility(View.GONE);
                        if (response.contains("PDOException")) {
                            Snackbar.make(coordinatorLayout, R.string.error_occured, Snackbar.LENGTH_LONG)
                                    .setAction(R.string.btn_refresh, null).show();

                        } else {
                            if (response.isEmpty() || response.equalsIgnoreCase("[]")) {
                                empty.setVisibility(View.VISIBLE);

                            } else {
                                try {
                                    //JSONArray jsonArray = new JSONArray(response);
                                    Type listType = new TypeToken<List<Reviews>>() {
                                    }.getType();
                                    boolean show = false;
                                    riders = new Gson().fromJson(response, listType);
                                    if (riders.size() > 15) {
                                        reviewsAdapter = new ReviewsAdapter(riders.subList(0, 14));
                                        nextPosition = 15;
                                        show = true;
                                    } else {
                                        reviewsAdapter = new ReviewsAdapter(riders,getContext());
                                    }
                                    empty.setVisibility(View.GONE);
                                    recyclerView.setAdapter(reviewsAdapter);
                                    if (show) {
                                        loadmoreButton.setVisibility(View.VISIBLE);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();

                                    Snackbar.make(coordinatorLayout, R.string.error_occured, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.btn_refresh, null).show();

                                }
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Snackbar.make(coordinatorLayout, R.string.error_occured, Snackbar.LENGTH_LONG)
                                .setAction(R.string.btn_refresh, null).show();
                    }

                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                //Adding parameters to request

                params.put("phone", phone);

                //returning parameter.
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

}
