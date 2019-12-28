package com.ygaps.travelapp.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ygaps.travelapp.R;

import java.util.ArrayList;

public class ListViewMembersAdapter extends BaseAdapter {
    ArrayList<MembersInfo> membersInfos;
    Activity activity;
    @Override
    public int getCount() {
        return membersInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return membersInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = activity.getLayoutInflater();
        view =inflater.inflate(R.layout.list_view_tour_member_item, null);

        TextView nameMember = view.findViewById(R.id.user_name_member);
        TextView phoneNumber = view.findViewById(R.id.phone_num_tour_member);

        nameMember.setText(membersInfos.get(i).getUserName());
        phoneNumber.setText(membersInfos.get(i).getPhone());

        return view;
    }

    public ListViewMembersAdapter(@NonNull Activity activity, @NonNull ArrayList<MembersInfo> membersInfos) {

        this.activity = activity;
        this.membersInfos = membersInfos;
    }

}
