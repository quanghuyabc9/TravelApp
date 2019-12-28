package com.ygaps.travelapp.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ygaps.travelapp.R;

import java.util.ArrayList;

import static com.ygaps.travelapp.utils.DateTimeTool.convertMillisToDateTime;


public class RecyclerTourCommentsAdapter extends RecyclerView.Adapter<RecyclerTourCommentsAdapter.ReviewDataHolder> {

    private ArrayList<CommentTourInfo> commentTourInfos;

    public class ReviewDataHolder extends RecyclerView.ViewHolder{

        private ImageView userAvatar;
        private TextView userName;
        private TextView createDate;
        private TextView feedBack;
        private ImageButton report;


        public ReviewDataHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.user_avatar_review_explore);
            userName = itemView.findViewById(R.id.user_name_review_explore);

            createDate = itemView.findViewById(R.id.create_date_review_explore);
            feedBack = itemView.findViewById(R.id.feedback_review_explore);

        }
    }

    public RecyclerTourCommentsAdapter(ArrayList<CommentTourInfo> commentTourInfos){
        this.commentTourInfos = commentTourInfos;
    }

    @NonNull
    @Override
    public ReviewDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rview_tour_detail_comment_item, parent, false);
        return new ReviewDataHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewDataHolder holder, int position) {
        CommentTourInfo currentItem = commentTourInfos.get(position);
        holder.userName.setText(currentItem.getUserName());
        holder.createDate.setText(convertMillisToDateTime(currentItem.getCreateOn()));
        holder.feedBack.setText(currentItem.getComment());
    }

    @Override
    public int getItemCount() {
        return commentTourInfos.size();
    }



}
