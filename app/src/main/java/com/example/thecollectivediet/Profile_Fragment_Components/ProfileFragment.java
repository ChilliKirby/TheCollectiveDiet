package com.example.thecollectivediet.Profile_Fragment_Components;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.thecollectivediet.R;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedStateInstance){
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        return v;
    }
}