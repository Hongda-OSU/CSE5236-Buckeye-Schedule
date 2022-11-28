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
import com.cse5236.buckeyeschedule.activities.SplashScreenActivity;
import com.cse5236.buckeyeschedule.databinding.FragmentAccountBinding;
import com.cse5236.buckeyeschedule.utilities.Constants;
import com.google.firebase.firestore.DocumentSnapshot;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private DocumentSnapshot documentSnapshot;
    private AlertDialog.Builder builder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (Constants.LANGUAGE == "zh"){
            ((MainActivity) getActivity()).setTitle("账户设置");
        } else {
            ((MainActivity) getActivity()).setTitle("Account Setting");
        }
        ((MainActivity) getActivity()).miniIconHelper(false);
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        builder = new AlertDialog.Builder(getActivity());
        View v = binding.getRoot();
        //Toast.makeText(getActivity(), "Account Fragment", Toast.LENGTH_SHORT).show();
        loadUserDetails();
        setListeners();
        Log.d("fragment lifecycle", "AccountFragment onCreateView invoked");
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment lifecycle", "AccountFragment onDestroyView invoked");
    }

    private void loadUserDetails() {
        binding.userNameFragment.setText(((MainActivity) getActivity()).preferenceManager.getString(Constants.KEY_NAME));
        ((MainActivity) getActivity()).currentUser.get()
                .addOnCompleteListener(task -> {
                    documentSnapshot = task.getResult();
                    binding.emailFragment.setText(documentSnapshot.getString(Constants.KEY_EMAIL));
                    binding.passwordFragment.setText(documentSnapshot.getString(Constants.KEY_PASSWORD));
                })
                .addOnFailureListener(e -> binding.emailFragment.setText("Unable to get user information."));
        ;
        byte[] bytes = Base64.decode(((MainActivity) getActivity()).preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageFragment.setImageBitmap(bitmap);
    }

    private void setListeners() {
        binding.passwordShowFragment.setOnClickListener(v -> {
            if (binding.passwordFragment.getInputType() != InputType.TYPE_CLASS_TEXT) {
                binding.passwordFragment.setInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                binding.passwordFragment.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });
        binding.buttonSignOutFragment.setOnClickListener(v -> ((MainActivity) getActivity()).signOut());
        binding.updatePasswordFragment.setOnClickListener(v -> {
            if (isValidUpdatePassword()) {
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
        binding.changeLanguage.setOnClickListener(v -> {
            final String[] listItems = {"English", "中文"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            int i = 0;
            if (Constants.LANGUAGE == "zh") {
                i = 1;
                builder.setTitle("选择语言...");
            } else {
                builder.setTitle("Choose Language...");
            }
            builder.setSingleChoiceItems(listItems, i, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String lang = Constants.LANGUAGE;
                    if (i == 0) {
                        Constants.LANGUAGE = "en";
                    } else if (i == 1) {
                        Constants.LANGUAGE = "zh";
                    }
                    if (lang != Constants.LANGUAGE) {
                        Intent intent = new Intent(getActivity(), SplashScreenActivity.class);
                        getActivity().startActivity(intent);
                    }
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }


    private void updateAccount() {
        ((MainActivity) getActivity()).currentUser
                .update(Constants.KEY_PASSWORD, binding.passwordFragment.getText().toString())
                .addOnSuccessListener(unused -> {
                    ((MainActivity) getActivity()).signOut();
                })
                .addOnFailureListener(e -> showToast("Unable to update password"));
    }

    private void deleteAccount() {
        ((MainActivity) getActivity()).currentUser
                .delete()
                .addOnSuccessListener(unused -> {
                    ((MainActivity) getActivity()).preferenceManager.clear();
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