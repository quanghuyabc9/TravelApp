package com.ygaps.travelapp.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ygaps.travelapp.R;
import com.ygaps.travelapp.utils.EditTool;

public class TourDetailMemberFragment extends Fragment {

    View view;

    public TourDetailMemberFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tourdetail_member, container, false);
        EditTool.HideSoftKeyboard(view.getContext());
        return view;
    }
}
