package com.imeja.carpooling.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.imeja.carpooling.R;
import com.imeja.carpooling.coolmap.MapsActivity;

import java.util.List;

public class PlaceSearchFragment extends DialogFragment {

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private PlacesSearchAdapter adapter;
    private RecyclerView recyclerView;

    public static PlaceSearchFragment newInstance() {
        return new PlaceSearchFragment();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_places_list, container, false);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set the adapter
        recyclerView = view.findViewById(R.id.list);
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), LinearLayout.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.grey_divider));
        recyclerView.addItemDecoration(decoration);
        adapter = new PlacesSearchAdapter((MapsActivity) getActivity());
        recyclerView.setAdapter(adapter);
    }

    public void setPredictions(List<AutocompletePrediction> predictions) {
        Log.e("PlaceFragment", "new predictions size is " + predictions.size());
        adapter.mValues.clear();
        adapter.mValues.addAll(predictions);
        adapter.notifyDataSetChanged();
    }

}
