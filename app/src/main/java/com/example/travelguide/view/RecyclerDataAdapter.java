package com.example.travelguide.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguide.R;

import java.util.ArrayList;

public class RecyclerDataAdapter extends RecyclerView.Adapter<RecyclerDataAdapter.DataViewHolder>{

    private ArrayList<TourItem> tourItems;

    public class DataViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImageView;
        private TextView location;
        private TextView date;
        private TextView quantity;
        private TextView price;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.tour_imview);
            location = itemView.findViewById(R.id.tour_location);
            date = itemView.findViewById(R.id.tour_date);
            quantity = itemView.findViewById(R.id.tour_quantity);
            price = itemView.findViewById(R.id.tour_price);
        }
    }

    public RecyclerDataAdapter(ArrayList<TourItem> tourItems){
        this.tourItems = tourItems;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        DataViewHolder dvh = new DataViewHolder(v);
        return dvh;

    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        TourItem currentItem = tourItems.get(position);
        holder.mImageView.setImageResource(currentItem.getImg_src());
        holder.location.setText(currentItem.getLocation());
        holder.date.setText(currentItem.getDate());
        holder.quantity.setText(currentItem.getQuantity());
        holder.price.setText(currentItem.getPrice());
    }

    @Override
    public int getItemCount() {
        return tourItems.size();
    }



}
