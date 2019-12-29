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

public class ListViewAdapterMessage extends BaseAdapter {
    public ArrayList<MessageItem> items;
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
        view =inflater.inflate(R.layout.list_view_item_search_user, null);
        TextView spName =view.findViewById(R.id.name_user_result);
        TextView spEmail =view.findViewById(R.id.email_user_result);

        spName.setText(items.get(i).getUserName());
        spEmail.setText(items.get(i).getNotification());

        return view;
    }

    public ListViewAdapterMessage(@NonNull Activity activity, @NonNull ArrayList<MessageItem> items) {
        this.activity=activity;
        this.items = items;
    }


}
