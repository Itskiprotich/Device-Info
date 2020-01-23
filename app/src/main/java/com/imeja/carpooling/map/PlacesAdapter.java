package com.imeja.carpooling.map;

import android.app.Activity;
import android.graphics.Color;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.imeja.carpooling.MainActivity;
import com.imeja.carpooling.R;

import java.util.ArrayList;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder> {

    public ArrayList<AutocompletePrediction> mValues = new ArrayList<AutocompletePrediction>();
    private MainActivity mapsActivity;

    public PlacesAdapter(MainActivity mapsActivity) {
        this.mapsActivity = mapsActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_place, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AutocompletePrediction prediction = mValues.get(position);
        CharSequence primary = prediction.getPrimaryText(new ForegroundColorSpan(Color.BLACK));
        CharSequence secondary = prediction.getSecondaryText(null);
        holder.mIdView.setText(primary);
        holder.mContentView.setText(secondary);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mIdView, mContentView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mIdView = itemView.findViewById(R.id.primary_tv);
            mContentView = itemView.findViewById(R.id.secondary_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AutocompletePrediction item = mValues.get(getAdapterPosition());
                    mapsActivity.newLocation(item);
                    InputMethodManager inputMethodManager = (InputMethodManager) mapsActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(mapsActivity.getCurrentFocus().getWindowToken(), 0);
                }
            });
        }
    }
}