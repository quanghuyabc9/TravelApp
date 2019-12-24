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
import java.util.List;

public class ListViewResultLocationAdapter extends BaseAdapter {
    ArrayList<StopPointInfo> items;
    Activity activity;
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = activity.getLayoutInflater();
        view =inflater.inflate(R.layout.list_view_item_search_location, null);
        TextView spName =view.findViewById(R.id.name_search_location_result);
        TextView spAddress =view.findViewById(R.id.address_search_location_result);

        spName.setText(items.get(i).getName());
        spAddress.setText(items.get(i).getAddress());

        return view;
    }

    public ListViewResultLocationAdapter(@NonNull Activity activity, @NonNull ArrayList<StopPointInfo> items) {

        this.activity=activity;
        this.items = items;
    }

}
