package com.cse5236.buckeyeschedule.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cse5236.buckeyeschedule.R;

public class AccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Toast.makeText(getActivity(), "Account Fragment", Toast.LENGTH_SHORT).show();
        Log.d("fragment lifecycle","AccountFragment onCreateView invoked");
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment lifecycle","AccountFragment onDestroyView invoked");
    }
}