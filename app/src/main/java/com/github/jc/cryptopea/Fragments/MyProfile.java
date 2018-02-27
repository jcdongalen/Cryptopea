package com.github.jc.cryptopea.Fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.jc.cryptopea.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfile extends Fragment {

    private View view;

    public MyProfile() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_profile, container, false);



        return view;
    }
}
