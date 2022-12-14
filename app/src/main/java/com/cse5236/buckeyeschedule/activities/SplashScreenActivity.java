package com.cse5236.buckeyeschedule.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cse5236.buckeyeschedule.R;
import com.cse5236.buckeyeschedule.databinding.ActivitySplashScreenBinding;
import com.cse5236.buckeyeschedule.utilities.Constants;
import com.cse5236.buckeyeschedule.wrapper.MyContextWrapper;

public class SplashScreenActivity extends AppCompatActivity {


    private ActivitySplashScreenBinding binding;
    private Animation topAnim, bottomAnim;
    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        topAnim.setDuration(1500);
        bottomAnim.setDuration(1500);

        imageView = binding.buckeyeScheduleIcon;
        textView = binding.textBuckeyeSchedule;

        imageView.setAnimation(topAnim);
        textView.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        }, Constants.SPLASH_SCREEN);

        Log.d("activity lifecycle","SplashScreenActivity onCreate invoked");
    }

    @Override
    protected void onStart() {
        super.onStart();
        isNetworkConnected();
        Log.d("activity lifecycle","SplashScreenActivity onStart invoked");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("activity lifecycle","SplashScreenActivity onResume invoked");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("activity lifecycle","SplashScreenActivity onPause invoked");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("activity lifecycle","SplashScreenActivity onStop invoked");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("activity lifecycle","SplashScreenActivity onRestart invoked");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("activity lifecycle","SplashScreenActivity onDestroy invoked");
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (!(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected())) {
            showToast("Network connectivity lost! Please connect to network");
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase, Constants.LANGUAGE));
    }
}