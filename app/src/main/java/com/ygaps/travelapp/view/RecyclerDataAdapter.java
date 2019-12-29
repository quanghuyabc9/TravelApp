package com.ygaps.travelapp.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ygaps.travelapp.R;

import java.util.ArrayList;

public class RecyclerDataAdapter extends RecyclerView.Adapter<RecyclerDataAdapter.DataViewHolder>{

    private ArrayList<TourItem> tourItems;
    private ClickListener clickListener;

    public class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private ImageView mImageView;
        private TextView location;
        private TextView date;
        private TextView quantity;
        private TextView price;


        public DataViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mImageView = itemView.findViewById(R.id.tour_imview);
            location = itemView.findViewById(R.id.tour_location);
            date = itemView.findViewById(R.id.tour_date);
            quantity = itemView.findViewById(R.id.tour_quantity);
            price = itemView.findViewById(R.id.tour_price);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        @Override
        public boolean onLongClick(View v) {
            clickListener.onItemLongClick(getAdapterPosition(), v);
            return false;
        }
    }

    public RecyclerDataAdapter(ArrayList<TourItem> tourItems){
        this.tourItems = tourItems;
    }
    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
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



    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

}