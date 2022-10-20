package com.cse5236.buckeyeschedule.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cse5236.buckeyeschedule.R;
import com.cse5236.buckeyeschedule.activities.MainActivity;

public class SearchFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setTitle("Search");
        ((MainActivity)getActivity()).miniIconHelper(true);
        // Inflate the layout for this fragment
        // Toast.makeText(getActivity(), "Search Fragment", Toast.LENGTH_SHORT).show();
        Log.d("fragment lifecycle","SearchFragment onCreateView invoked");
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment lifecycle","SearchFragment onDestroyView invoked");
    }
}