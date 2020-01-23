package com.imeja.carpooling.riderprofile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.imeja.carpooling.R;
import com.imeja.carpooling.holders.ReviewsViewHolder;
import com.imeja.carpooling.holders.RidersViewHolder;
import com.imeja.carpooling.model.Reviews;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter {
    private List<Reviews> ridersList;
    Context context;

    public ReviewsAdapter(List<Reviews> ridersList, Context context) {
        this.ridersList = ridersList;
        this.context = context;

    }

    public ReviewsAdapter(List<Reviews> subList) {
        this.ridersList = subList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReviewsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.riders_reviews, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ReviewsViewHolder) {
            Reviews riders = ridersList.get(position);

            ((ReviewsViewHolder) holder).name.setText(riders.name);
            ((ReviewsViewHolder) holder).email.setText(riders.comment);
            ((ReviewsViewHolder) holder).reviews.setRating(Float.parseFloat(riders.rating));

            if (riders.profile != null) {

                Glide.with(context)
                        .load(riders.profile)
                        .apply(new RequestOptions()
                                .error(R.drawable.profile)
                                .fallback(R.drawable.profile)
                                .centerCrop()
                                .circleCrop())
                        .into(((RidersViewHolder) holder).profile);
            } else {
                /*((ReviewsViewHolder)holder).profile.setImageDrawable(context.getDrawable(R.drawable.profile));*/
            }
        }

    }

    @Override
    public int getItemCount() {
        return ridersList.size();
    }

    public void addItems(List<Reviews> subList) {
        ridersList.addAll(subList);
        notifyItemInserted(ridersList.size());

    }
}
