package com.imeja.carpooling.searching;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.imeja.carpooling.R;
import com.imeja.carpooling.api.ApiListener;
import com.imeja.carpooling.api.ApiRepository;
import com.imeja.carpooling.model.AppUtils;
import com.imeja.carpooling.model.RealmUtils;
import com.imeja.carpooling.net.Endpoints;
import com.imeja.carpooling.searching.adapters.RidersAdapter;
import com.imeja.carpooling.searching.model.MembersModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {

    private String TAG = SearchActivity.class.getSimpleName();
    private RecyclerView comRecyclerView;
    private RidersAdapter commissionsAdapter;
    ArrayList<MembersModel> modelmembersArrayList;
    private ProgressDialog pDialog;
    private ApiRepository apiRepository;
    private int nextPosition = -1;
    private SwipeRefreshLayout swpRefreshLayout;
    private Double totalCommission = 0.0;
    Toolbar toolbar;

    Button loadmoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        comRecyclerView = findViewById(R.id.rv_commissions);
        apiRepository = ApiRepository.getInstance();
        loadmoreButton=findViewById(R.id.loadmoreButton);
        modelmembersArrayList = new ArrayList<>();
        commissionsAdapter = new RidersAdapter(modelmembersArrayList);
        loadmoreButton.setVisibility(View.GONE);
        loadMembers();
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
                        AppUtils.showInfoDialog(SearchActivity.this, "Members",
                                "No  Members  available", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                        modelmembersArrayList.clear();
                                        commissionsAdapter.notifyDataSetChanged();

                                    }
                                });
                    } else {
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
                            Toast.makeText(SearchActivity.this, "An error has occurred retrieving your Members.Please try again later.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                } else {
                    AppUtils.showInfoDialog(SearchActivity.this, "Error",
                            "An error occurred while loading your Members, please try again", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // finish();
                                }
                            });
                }
                dismissDialog();
            }
        }, SearchActivity.this);

    }


    public void showDialog() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
    }

    public void dismissDialog() {
        pDialog.dismiss();
    }

    public void loadMore(View view) {
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





