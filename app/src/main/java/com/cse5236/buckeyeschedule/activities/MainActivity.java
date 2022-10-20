package com.cse5236.buckeyeschedule.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cse5236.buckeyeschedule.R;
import com.cse5236.buckeyeschedule.databinding.ActivityMainBinding;
import com.cse5236.buckeyeschedule.fragments.AccountFragment;
import com.cse5236.buckeyeschedule.fragments.ScheduleFragment;
import com.cse5236.buckeyeschedule.fragments.SearchFragment;
import com.cse5236.buckeyeschedule.utilities.Constants;
import com.cse5236.buckeyeschedule.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
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
        Log.d("Main activity lifecycle","MainActivity onDestroy invoked");
    }

    private void setListeners() {
        binding.imageSignOut.setOnClickListener(v -> signOut());
        binding.imageProfile.setOnClickListener(v -> {
            replaceFragment(new AccountFragment(), "Account");
        });
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.schedule:
                    replaceFragment(new ScheduleFragment(), "Schedule");
                    break;
                case R.id.search:
                    replaceFragment(new SearchFragment(), "Search");
                    break;
                case R.id.account:
                    replaceFragment(new AccountFragment(), "Account");
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment, String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, tag);
        fragmentTransaction.addToBackStack(null);
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

    private void signOut() {
        showToast("Signing out...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> updates = new HashMap<>();
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Unable to sign out"));
    }

    public void displayHelper(Boolean display) {
        if (!display) {
            binding.imageSignOut.setVisibility(View.INVISIBLE);
            binding.imageProfile.setVisibility(View.INVISIBLE);
        } else {
            binding.imageSignOut.setVisibility(View.VISIBLE);
            binding.imageProfile.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentByTag("Account");
        if (f instanceof AccountFragment && f.isVisible()) {
            binding.imageSignOut.setVisibility(View.VISIBLE);
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
}