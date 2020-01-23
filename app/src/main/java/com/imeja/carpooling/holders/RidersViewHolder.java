package com.imeja.carpooling.holders;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imeja.carpooling.R;

public class RidersViewHolder extends RecyclerView.ViewHolder {

    public ImageView profile;
    public TextView name;
    public TextView email;
    public TextView start;
    public TextView end;
    public TextView pickup;
    public TextView drop;
    public TextView total;
    public  TextView seats;
    public TextView price;
    public RatingBar reviews;
    public Button request;


    public RidersViewHolder(@NonNull View itemView) {
        super(itemView);

        profile= itemView.findViewById(R.id.profile);
        name= itemView.findViewById(R.id.name);
        email= itemView.findViewById(R.id.email);
        start= itemView.findViewById(R.id.start);
        end= itemView.findViewById(R.id.end);
        drop= itemView.findViewById(R.id.drop);
        pickup= itemView.findViewById(R.id.pickup);
        total= itemView.findViewById(R.id.total);
        seats= itemView.findViewById(R.id.seats);
        price= itemView.findViewById(R.id.price);
        reviews= itemView.findViewById(R.id.reviews);
        request= itemView.findViewById(R.id.request);

    }
}
