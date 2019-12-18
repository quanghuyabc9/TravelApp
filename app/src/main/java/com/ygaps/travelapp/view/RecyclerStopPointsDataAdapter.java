package com.ygaps.travelapp.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ygaps.travelapp.R;

import java.util.ArrayList;

import static com.ygaps.travelapp.utils.DateTimeTool.convertMillisToDateTime;

public class RecyclerStopPointsDataAdapter extends RecyclerView.Adapter< RecyclerStopPointsDataAdapter.spDataViewHolder> {

    private ArrayList<StopPointInfo> spDataItems;

    public class spDataViewHolder extends RecyclerView.ViewHolder{

        private TextView spName;
        private TextView spAddress;
        private TextView spTime;

        public spDataViewHolder(@NonNull View itemView) {
            super(itemView);
            spName = itemView.findViewById(R.id.sp_rv_item_name);
            spAddress = itemView.findViewById(R.id.sp_rv_item_address);
            spTime = itemView.findViewById(R.id.sp_rv_time);
        }
    }

    public RecyclerStopPointsDataAdapter(ArrayList<StopPointInfo> spDataItems){
        this.spDataItems = spDataItems;
    }

    @NonNull
    @Override
    public spDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.stop_point_rv_item, parent, false);
        return new spDataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull spDataViewHolder holder, int position) {
        StopPointInfo currentItem = spDataItems.get(position);
        holder.spName.setText(currentItem.getName());
        holder.spAddress.setText(currentItem.getAddress());
        holder.spTime.setText(convertMillisToDateTime(currentItem.getArriveAt()) + " - " + convertMillisToDateTime(currentItem.getLeaveAt()));
    }

    @Override
    public int getItemCount() {
        return spDataItems.size();
    }

}
