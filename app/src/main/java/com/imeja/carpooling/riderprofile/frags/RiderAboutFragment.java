package com.imeja.carpooling.riderprofile.frags;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.imeja.carpooling.R;
import com.imeja.carpooling.confirmride.ConfirmRideActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class RiderAboutFragment extends Fragment {

    Button request_ride;


    public RiderAboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rider_about, container, false);
        request_ride = view.findViewById(R.id.request_ride);
        request_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), ConfirmRideActivity.class));
                getActivity().finish();
            }
        });
        return view;
    }

}
