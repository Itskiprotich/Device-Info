package com.imeja.carpooling.searching.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imeja.carpooling.R;
import com.imeja.carpooling.searching.model.MembersModel;

import java.util.List;

public class RidersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MembersModel> items;

    public RidersAdapter(List<MembersModel> items) {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof MembersModel) {
            return 1;
        } else {
            return super.getItemViewType(position);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommissionsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.available_riders, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CommissionsHolder) {
            MembersModel commissions = items.get(position);
            ((CommissionsHolder) holder).dateTv.setText(commissions.Phone.trim());
            ((CommissionsHolder) holder).loanAmountTv.setText(commissions.Name.trim());
            ((CommissionsHolder) holder).commissionTv.setText(String.valueOf(commissions.Amount).trim());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(MembersModel commissions) {
        items.add(commissions);
        notifyItemInserted(items.size());
    }

    public void addItems(List<MembersModel> subList) {

        items.addAll(subList);
        notifyItemInserted(items.size());
    }

    public class CommissionsHolder extends RecyclerView.ViewHolder {
        public TextView dateTv, loanAmountTv, commissionTv;

        public CommissionsHolder(@NonNull View itemView) {
            super(itemView);
            dateTv = itemView.findViewById(R.id.name);
            loanAmountTv = itemView.findViewById(R.id.email);
            commissionTv = itemView.findViewById(R.id.seats);
        }
    }
}