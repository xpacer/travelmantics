package com.xpacer.travelmantics.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;
import com.xpacer.travelmantics.DealActivity;
import com.xpacer.travelmantics.R;
import com.xpacer.travelmantics.models.TravelDeal;
import com.xpacer.travelmantics.utils.FirebaseUtil;

import java.util.ArrayList;

public class DealAdapter extends Adapter<DealAdapter.DealViewHolder> {

    private ArrayList<TravelDeal> travelDeals;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;

    public DealAdapter() {
        travelDeals = FirebaseUtil.mDeals;
        mDatabaseReference = FirebaseUtil.mDatabaseReference;
        mChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal deal = dataSnapshot.getValue(TravelDeal.class);
                deal.setId(dataSnapshot.getKey());
                travelDeals.add(deal);
                notifyItemInserted(travelDeals.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View itemView = layoutInflater.inflate(R.layout.item_layout_deal, viewGroup, false);

        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder dealViewHolder, int i) {
        dealViewHolder.bind(travelDeals.get(i));
    }

    @Override
    public int getItemCount() {
        return travelDeals.size();
    }

    public class DealViewHolder extends ViewHolder implements View.OnClickListener {
        TextView tvTitle;
        TextView tvDescription;
        TextView tvPrice;
        ImageView ivDeal;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivDeal = itemView.findViewById(R.id.iv_deal);

            itemView.setOnClickListener(this);
        }

        public void bind(TravelDeal deal) {
            tvTitle.setText(deal.getTitle());
            tvPrice.setText(deal.getPrice());
            tvDescription.setText(deal.getDescription());
            showImage(deal.getImageUrl());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            TravelDeal selectedDeal = travelDeals.get(position);

            Intent intent = new Intent(view.getContext(), DealActivity.class);
            intent.putExtra("Deal", selectedDeal);
            view.getContext().startActivity(intent);
        }

        private void showImage(String url) {
            if (url != null && !url.isEmpty()) {
                int width = Resources.getSystem().getDisplayMetrics().widthPixels;
                Picasso.get()
                        .load(url)
                        .resize(160, 160)
                        .centerCrop()
                        .into(ivDeal);
            }
        }
    }
}
