package com.cse5236.buckeyeschedule.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cse5236.buckeyeschedule.R;
import com.cse5236.buckeyeschedule.databinding.ActivityMainBinding;
import com.cse5236.buckeyeschedule.fragments.AccountFragment;
import com.cse5236.buckeyeschedule.fragments.ScheduleFragment;
import com.cse5236.buckeyeschedule.utilities.Constants;
import com.cse5236.buckeyeschedule.utilities.PreferenceManager;
import com.cse5236.buckeyeschedule.wrapper.MyContextWrapper;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public PreferenceManager preferenceManager;
    public FirebaseFirestore database;
    public DocumentReference currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isNetworkConnected();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        database = FirebaseFirestore.getInstance();
        currentUser = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        replaceFragment(new ScheduleFragment(), "Schedule");
        loadUserDetails();
        setListeners();
        Log.d("activity lifecycle","MainActivity onCreate invoked");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Main activity lifecycle","MainActivity onStart invoked");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Main activity lifecycle","MainActivity onResume invoked");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Main activity lifecycle","MainActivity onPause invoked");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Main activity lifecycle","MainActivity onStop invoked");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Main activity lifecycle","MainActivity onRestart invoked");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        showToast("Buckeye Schedule terminated!");
        Log.d("Main activity lifecycle","MainActivity onDestroy invoked");
    }

    private void setListeners() {
        //binding.imageSignOut.setOnClickListener(v -> signOut());
        binding.imageProfile.setOnClickListener(v -> {
            replaceFragment(new AccountFragment(), "Account");
            binding.imageBack.setVisibility(View.VISIBLE);
        });
        binding.imageBack.setOnClickListener(v -> {
            replaceFragment(new ScheduleFragment(), "Schedule");
            binding.imageBack.setVisibility(View.INVISIBLE);
        });
//        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
//            switch (item.getItemId()) {
//                case R.id.schedule:
//                    replaceFragment(new ScheduleFragment(), "Schedule");
//                    break;
//                case R.id.search:
//                    replaceFragment(new SearchFragment(), "Search");
//                    break;
//                case R.id.account:
//                    replaceFragment(new AccountFragment(), "Account");
//                    break;
//            }
//            return true;
//        });
    }

    public void replaceFragment(Fragment fragment, String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, tag);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.commit();
    }

    private void loadUserDetails() {
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void signOut() {
        showToast("Signing out...");
        HashMap<String, Object> updates = new HashMap<>();
        currentUser.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Unable to sign out"));
    }

    public void miniIconHelper(Boolean display) {
        if (!display) {
            //binding.imageSignOut.setVisibility(View.INVISIBLE);
            binding.imageProfile.setVisibility(View.INVISIBLE);
        } else {
            //binding.imageSignOut.setVisibility(View.VISIBLE);
            binding.imageProfile.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentByTag("Account");
        if (f instanceof AccountFragment && f.isVisible()) {
            //binding.imageSignOut.setVisibility(View.VISIBLE);
            binding.imageProfile.setVisibility(View.VISIBLE);
        }
        if(getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        }
        else{
            super.onBackPressed();
        }
    }

    public void setTitle(String title) {
        binding.textName.setText(title);
    }

    public void isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (!(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected())) {
            showToast("Network connectivity lost! Please connect to network");
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(MyContextWrapper.wrap(newBase,Constants.LANGUAGE));
    }
}