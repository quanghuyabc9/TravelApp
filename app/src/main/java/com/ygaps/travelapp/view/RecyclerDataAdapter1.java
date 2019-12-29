package com.ygaps.travelapp.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ygaps.travelapp.R;

import java.util.ArrayList;

public class RecyclerDataAdapter1 extends RecyclerView.Adapter<RecyclerDataAdapter1.DataViewHolder>{

    private ArrayList<CommentItem> commentItems;
    private ClickListener clickListener;

    public class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView name;
        private TextView comment;
        private TextView onTime;


        public DataViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            name = itemView.findViewById(R.id.mTourName);
            onTime = itemView.findViewById(R.id.tour_time);
            comment=itemView.findViewById(R.id.tour_comment);
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

    public RecyclerDataAdapter1(ArrayList<CommentItem> commentItems){
        this.commentItems=commentItems;
    }
    public void setOnItemClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_item, parent, false);
        DataViewHolder dvh = new DataViewHolder(v);
        return dvh;

    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        CommentItem currentItem = commentItems.get(position);
        holder.name.setText(currentItem.getName());
        holder.onTime.setText(currentItem.getTime());
        holder.comment.setText(currentItem.getComment());
    }


    @Override
    public int getItemCount() {
        return commentItems.size();
    }



    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }

}