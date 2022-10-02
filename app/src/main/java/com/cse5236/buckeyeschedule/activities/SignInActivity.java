package com.cse5236.buckeyeschedule.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.cse5236.buckeyeschedule.R;
import com.cse5236.buckeyeschedule.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}