package com.cse5236.buckeyeschedule.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cse5236.buckeyeschedule.activities.MainActivity;
import com.cse5236.buckeyeschedule.databinding.FragmentAccountBinding;
import com.cse5236.buckeyeschedule.utilities.Constants;
import com.cse5236.buckeyeschedule.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private DocumentReference currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setTitle("Account Setting");
        ((MainActivity)getActivity()).displayHelper(false);
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        preferenceManager = new PreferenceManager(getActivity().getApplicationContext());
        database = FirebaseFirestore.getInstance();
        currentUser = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        //Toast.makeText(getActivity(), "Account Fragment", Toast.LENGTH_SHORT).show();
        loadUserDetails();
        setListeners();
        Log.d("fragment lifecycle","AccountFragment onCreateView invoked");
        return v;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment lifecycle","AccountFragment onDestroyView invoked");
    }

    private void loadUserDetails() {
        binding.usernameAcctFragment.setText(preferenceManager.getString(Constants.KEY_NAME));
        currentUser.get()
                .addOnCompleteListener(task -> {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    binding.emailAcctFragment.setText(documentSnapshot.getString(Constants.KEY_EMAIL));
                    binding.passwordAcctFragment.setText(documentSnapshot.getString(Constants.KEY_PASSWORD));
                })
                .addOnFailureListener(e -> binding.emailAcctFragment.setText("Unable to get user email."));;
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageAcctFragment.setImageBitmap(bitmap);
    }

    private void setListeners() {
        binding.passwordShowAcctFragment.setOnClickListener(v -> {
            if (binding.passwordAcctFragment.getInputType() != InputType.TYPE_CLASS_TEXT) {
                binding.passwordAcctFragment.setInputType(InputType.TYPE_CLASS_TEXT );
            } else {
                binding.passwordAcctFragment.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
    }


}