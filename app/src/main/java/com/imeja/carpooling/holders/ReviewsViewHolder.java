package com.imeja.carpooling.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imeja.carpooling.R;

public class ReviewsViewHolder extends RecyclerView.ViewHolder {

    public ImageView profile;
    public TextView name;
    public RatingBar reviews;
    public TextView email;

    public ReviewsViewHolder(@NonNull View itemView) {
        super(itemView);
        profile = itemView.findViewById(R.id.profile);
        name = itemView.findViewById(R.id.name);
        reviews = itemView.findViewById(R.id.reviews);
        email = itemView.findViewById(R.id.email);
    }
}
