package com.github.jc.cryptopea.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.jc.cryptopea.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUs extends Fragment {

    View view;

    public AboutUs() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = getView() != null ? getView() : inflater.inflate(R.layout.fragment_about_us, container, false);
        view.findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Button) v).setText("MUSTNOTTEST");
            }
        });
        return view;
    }

}
