package com.imeja.carpooling.api;

import android.app.Activity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.imeja.carpooling.trustmanager.HttpsTrustManager;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ApiRepository {

    private static final String TAG = ApiRepository.class.getSimpleName();
    private static ApiService apiService;
    private static ApiRepository apiRepository;
    private static Executor executor;

    private ApiRepository() {

    }

    public static ApiRepository getInstance() {
        if (apiRepository == null) {
            apiRepository = new ApiRepository();
            executor = Executors.newFixedThreadPool(3);

        }
        return apiRepository;
    }

    public void request(final Map<String, String> params, int type,
                        String url, final ApiListener apiListener, Activity activity) {

        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(type, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "onResponse: " + response);
                        apiListener.onResponse(response, null);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: volley error is: " + error.getMessage());
                        apiListener.onResponse(null, error.getMessage());
                    }
                }) {
            protected Map<String, String> getParams() {
                return params;
            }
        };

        //Adding the string request to the queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        requestQueue.getCache().clear();
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 3, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }
}