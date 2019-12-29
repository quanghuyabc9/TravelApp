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


public class RecyclerTourReviewsAdapter extends RecyclerView.Adapter<RecyclerTourReviewsAdapter.ReviewDataHolder> {

    private ArrayList<ReviewsTourInfo> reviewsInfoItems;

    public class ReviewDataHolder extends RecyclerView.ViewHolder{

        private ImageView userAvatar;
        private TextView userName;
        ArrayList<ImageView> stars;
        private TextView createDate;
        private TextView feedBack;
        private ImageButton report;


        public ReviewDataHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.user_avatar_review_explore);
            userName = itemView.findViewById(R.id.user_name_review_explore);
            stars = new ArrayList<>();
            stars.add( (ImageView) itemView.findViewById(R.id.star_review_explore_1));
            stars.add( (ImageView) itemView.findViewById(R.id.star_review_explore_2));
            stars.add( (ImageView) itemView.findViewById(R.id.star_review_explore_3));
            stars.add( (ImageView) itemView.findViewById(R.id.star_review_explore_4));
            stars.add( (ImageView) itemView.findViewById(R.id.star_review_explore_5));

            createDate = itemView.findViewById(R.id.create_date_review_explore);
            feedBack = itemView.findViewById(R.id.feedback_review_explore);
            report = itemView.findViewById(R.id.btn_report_review_explore);

        }
    }
    public RecyclerTourReviewsAdapter(ArrayList<ReviewsTourInfo> reviewInfoItems){
        this.reviewsInfoItems = reviewInfoItems;
    }

    @NonNull
    @Override
    public ReviewDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.stop_point_info_tab2_rview_item, parent, false);
        return new ReviewDataHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewDataHolder holder, int position) {
        ReviewsTourInfo currentItem = reviewsInfoItems.get(position);
        holder.userName.setText(currentItem.getUserName());
        holder.createDate.setText(convertMillisToDateTime(currentItem.getCreateOn()));
        holder.feedBack.setText(currentItem.getReview());

        int point = currentItem.getPoint();
        for (int i = 0; i < point; i++){
            holder.stars.get(i).setImageResource(R.drawable.ic_star_rate_24dp);
        }
        //holder.spTime.setText(convertMillisToDateTime(currentItem.getArriveAt()) + " - " + convertMillisToDateTime(currentItem.getLeaveAt()));
    }

    @Override
    public int getItemCount() {
        return reviewsInfoItems.size();
    }



}
