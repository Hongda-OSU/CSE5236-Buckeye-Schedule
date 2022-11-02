package com.cse5236.buckeyeschedule.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cse5236.buckeyeschedule.activities.MainActivity;
import com.cse5236.buckeyeschedule.activities.SignInActivity;
import com.cse5236.buckeyeschedule.databinding.FragmentAccountBinding;
import com.cse5236.buckeyeschedule.utilities.Constants;
import com.cse5236.buckeyeschedule.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private PreferenceManager preferenceManager;
    private DocumentSnapshot documentSnapshot;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setTitle("Account Setting");
        ((MainActivity)getActivity()).miniIconHelper(false);
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        builder = new AlertDialog.Builder(getActivity());
        View v = binding.getRoot();
        preferenceManager = ((MainActivity)getActivity()).preferenceManager;
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
        binding.userNameFragment.setText(preferenceManager.getString(Constants.KEY_NAME));
        ((MainActivity)getActivity()).currentUser.get()
                .addOnCompleteListener(task -> {
                    documentSnapshot = task.getResult();
                    binding.emailFragment.setText(documentSnapshot.getString(Constants.KEY_EMAIL));
                    binding.passwordFragment.setText(documentSnapshot.getString(Constants.KEY_PASSWORD));
                })
                .addOnFailureListener(e -> binding.emailFragment.setText("Unable to get user information."));;
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageFragment.setImageBitmap(bitmap);
    }

    private void setListeners() {
        binding.passwordShowFragment.setOnClickListener(v -> {
            if (binding.passwordFragment.getInputType() != InputType.TYPE_CLASS_TEXT) {
                binding.passwordFragment.setInputType(InputType.TYPE_CLASS_TEXT );
            } else {
                binding.passwordFragment.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
        binding.buttonSignOutFragment.setOnClickListener(v -> ((MainActivity)getActivity()).signOut());
        binding.updatePasswordFragment.setOnClickListener(v -> {
            if (isValidUpdatePassword()){
                builder.setTitle("Update Password").setMessage("Are you sure?")
                        .setPositiveButton("Ok", (dialogInterface, i) -> {
                            updateAccount();
                        })
                        .setNegativeButton("Cancel", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        binding.deleteAccountFragment.setOnClickListener(v -> {
            builder.setTitle("Delete Account").setMessage("Are you sure?")
                    .setPositiveButton("Ok", (dialogInterface, i) -> {
                        deleteAccount();
                    })
                    .setNegativeButton("Cancel", null);
            AlertDialog alert = builder.create();
            alert.show();
        });
    }

    private void updateAccount() {
        ((MainActivity)getActivity()).currentUser
                .update(Constants.KEY_PASSWORD, binding.passwordFragment.getText().toString())
                .addOnSuccessListener(unused -> {
                    ((MainActivity)getActivity()).signOut();
                })
                .addOnFailureListener(e -> showToast("Unable to update password"));
    }

    private void deleteAccount() {
        ((MainActivity)getActivity()).currentUser
                .delete()
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getActivity(), SignInActivity.class));
                    getActivity().finish();
                })
                .addOnFailureListener(e -> showToast("Unable to delete account"));
    }

    private boolean isValidUpdatePassword() {
        if (binding.passwordFragment.getText().toString().trim().isEmpty()) {
            showToast("Password is empty");
            return false;
        } else if (binding.passwordFragment.getText().toString().equals(documentSnapshot.getString(Constants.KEY_PASSWORD))) {
            showToast("Password not change");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

}