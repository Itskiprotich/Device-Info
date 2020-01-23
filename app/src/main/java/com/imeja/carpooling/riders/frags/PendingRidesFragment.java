package com.imeja.carpooling.riders.frags;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.imeja.carpooling.api.ApiListener;
import com.imeja.carpooling.api.ApiRepository;
import com.imeja.carpooling.model.AppUtils;
import com.imeja.carpooling.model.RealmUtils;
import com.imeja.carpooling.model.Reviews;
import com.imeja.carpooling.net.Endpoints;
import com.imeja.carpooling.riderprofile.adapters.ReviewsAdapter;
import com.imeja.carpooling.searching.SearchActivity;
import com.imeja.carpooling.searching.adapters.RidersAdapter;
import com.imeja.carpooling.searching.model.MembersModel;
import com.imeja.carpooling.trustmanager.HttpsTrustManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingRidesFragment extends Fragment {

    private static final String TAG = PendingRidesFragment.class.getSimpleName();
    LinearLayout empty;
    private RecyclerView comRecyclerView;
    private RidersAdapter commissionsAdapter;
    ArrayList<MembersModel> modelmembersArrayList;
    private ProgressDialog pDialog;
    private ApiRepository apiRepository;
    private int nextPosition = -1;
    private Double totalCommission = 0.0;
    Button loadmoreButton;
RelativeLayout mRelativeLayout;

    public PendingRidesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending_rides, container, false);
        comRecyclerView = view.findViewById(R.id.recyclerView);
        apiRepository = ApiRepository.getInstance();
        mRelativeLayout=view.findViewById(R.id.mRelativeLayout);
        loadmoreButton = view.findViewById(R.id.loadmoreButton);
        empty = view.findViewById(R.id.empty);
        modelmembersArrayList = new ArrayList<>();
        commissionsAdapter = new RidersAdapter(modelmembersArrayList);
        loadmoreButton.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);
        loadmoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMore();
            }
        });
        loadMembers();
        return view;
    }

    private void loadMembers() {
        showDialog();
        Map<String, String> params = new HashMap<>();
        params.put("code", RealmUtils.getPhoneNumber());
        Log.e(TAG, "loadCommissions: " + params);
        apiRepository.request(params, Request.Method.POST, Endpoints.AVAILABLE_RIDERS_URL, new ApiListener() {
            @Override
            public void onResponse(String response, String error) {
                Log.e(TAG, " response: " + response);
                Log.e(TAG, " Error: " + error);
                modelmembersArrayList.clear();
                if (error == null && response != null) {
                    if (response.isEmpty() || response.equalsIgnoreCase("[]")) {
                        dismissDialog();
                        empty.setVisibility(View.VISIBLE);

                    } else {
                        empty.setVisibility(View.GONE);
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObjectcommission = jsonArray.getJSONObject(i);
                            }

                            Type listType = new TypeToken<List<MembersModel>>() {
                            }.getType();
                            boolean show = false;
                            modelmembersArrayList = new Gson().fromJson(response, listType);

                            if (modelmembersArrayList.size() > 15) {
                                commissionsAdapter = new RidersAdapter(modelmembersArrayList.subList(0, 14));
                                nextPosition = 15;
                                show = true;
                            } else {
                                commissionsAdapter = new RidersAdapter(modelmembersArrayList);
                            }
                            comRecyclerView.setAdapter(commissionsAdapter);
                            if (show) {
                                loadmoreButton.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                            Snackbar.make(mRelativeLayout, "An error has occurred retrieving your rides.Please try again later.", Snackbar.LENGTH_LONG)
                                    .setAction(R.string.btn_refresh, null).show();
                        }
                    }
                } else {

                    Snackbar.make(mRelativeLayout, "An error has occurred retrieving your rides.Please try again later.", Snackbar.LENGTH_LONG)
                            .setAction(R.string.btn_refresh, null).show();
                }
                dismissDialog();
            }
        }, (Activity) getContext());

    }


    public void showDialog() {
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
    }

    public void dismissDialog() {
        pDialog.dismiss();
    }

    public void loadMore() {
        showDialog();
        loadmoreButton.setVisibility(View.GONE);
        if (nextPosition != modelmembersArrayList.size() - 1) {
            final List<MembersModel> sub = modelmembersArrayList.subList(nextPosition, modelmembersArrayList.size());
            if (sub.size() > 15) {
                nextPosition += 15;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        commissionsAdapter.addItems(sub.subList(0, 14));
                        loadmoreButton.setVisibility(View.VISIBLE);
                        dismissDialog();
                    }
                }, 2000);

            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        commissionsAdapter.addItems(sub);
                        loadmoreButton.setVisibility(View.GONE);
                        dismissDialog();
                    }
                }, 2000);

            }
            Log.e(TAG, "new position is " + nextPosition);
        }
    }


}
