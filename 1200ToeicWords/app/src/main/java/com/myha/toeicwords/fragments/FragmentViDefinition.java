package com.myha.toeicwords.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myha.toeicwords.R;
import com.myha.toeicwords.WordMeaningActivity;


public class FragmentViDefinition extends Fragment {
    public FragmentViDefinition() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_definition,container, false);//Inflate Layout


        Context context=getActivity();
        TextView text = (TextView) view.findViewById(R.id.textViewD);

        String vi_definition= ((WordMeaningActivity)context).viDefinition;

        text.setText(vi_definition);
        if(vi_definition==null)
        {
            text.setText("Không load được nghĩa của từ này");
        }

        return view;
    }
}
