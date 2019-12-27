package com.ygaps.travelapp.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ygaps.travelapp.R;

import java.util.ArrayList;

import static com.ygaps.travelapp.utils.DateTimeTool.convertMillisToDateTime;

public class RecyclerStopPointTourDetailAdapter extends RecyclerView.Adapter<RecyclerStopPointTourDetailAdapter.spDataViewHolder> {

    private ArrayList<StopPointInfo> spDataItems;
    private Context context;

    public class spDataViewHolder extends RecyclerView.ViewHolder{

        private TextView spName;
        private TextView spServiceType;
        private ImageView spServiceTypeIcon;
        private TextView spAddress;
        private TextView spTime;
        private TextView spPrice;

        public spDataViewHolder(@NonNull View itemView) {
            super(itemView);
            spName = itemView.findViewById(R.id.sp_name_sp_tour_detail);
            spServiceType = itemView.findViewById(R.id.sp_service_type_tour_detail);
            spServiceTypeIcon = itemView.findViewById(R.id.sp_icon_service_type_tour_detail);
            spAddress = itemView.findViewById(R.id.sp_address_tour_detail);
            spTime = itemView.findViewById(R.id.sp_datetime_tour_detail);
            spPrice = itemView.findViewById(R.id.sp_price_tour_detail);
        }
    }

    public RecyclerStopPointTourDetailAdapter(ArrayList<StopPointInfo> spDataItems){
        this.spDataItems = spDataItems;
    }

    @NonNull
    @Override
    public RecyclerStopPointTourDetailAdapter.spDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.stop_point_tour_detail_rview, parent, false);
        // set the Context here
        context = parent.getContext();
        return new RecyclerStopPointTourDetailAdapter.spDataViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerStopPointTourDetailAdapter.spDataViewHolder holder, int position) {
        StopPointInfo currentItem = spDataItems.get(position);
        holder.spName.setText(currentItem.getName());


        String[] province = context.getResources().getStringArray(R.array.province);
        String[] serviceType = context.getResources().getStringArray(R.array.serviceName);
        int indexSvId = currentItem.getServiceTypeId() - 1;
        if (indexSvId >= serviceType.length || indexSvId < 0){
            indexSvId = 0;
        }

        switch (indexSvId){
            case 0:
                holder.spServiceTypeIcon.setImageResource(R.drawable.icons8_restaurant_100);
                break;
            case 1:
                holder.spServiceTypeIcon.setImageResource(R.drawable.icons8_hotel_building_100);
                break;
            case 2:
                holder.spServiceTypeIcon.setImageResource(R.drawable.icons8_empty_bed_96);
                break;
            case 3:
                holder.spServiceTypeIcon.setImageResource(R.drawable.icons8_service_mark_80);
                break;
        }

        holder.spServiceType.setText(serviceType[indexSvId]);

        holder.spAddress.setText(currentItem.getAddress());
        holder.spTime.setText(convertMillisToDateTime(currentItem.getArriveAt()) + " - " + convertMillisToDateTime(currentItem.getLeaveAt()));
        holder.spPrice.setText(currentItem.getMinCost() + " - " +currentItem.getMaxCost());
    }

    @Override
    public int getItemCount() {
        return spDataItems.size();
    }

}
