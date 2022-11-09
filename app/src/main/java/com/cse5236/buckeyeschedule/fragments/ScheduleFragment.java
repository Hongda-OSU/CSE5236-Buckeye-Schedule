package com.cse5236.buckeyeschedule.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.cse5236.buckeyeschedule.R;
import com.cse5236.buckeyeschedule.activities.MainActivity;
import com.cse5236.buckeyeschedule.adapters.ScheduleAdapter;
import com.cse5236.buckeyeschedule.databinding.FragmentScheduleBinding;
import com.cse5236.buckeyeschedule.entities.Schedule;
import com.cse5236.buckeyeschedule.factory.ScheduleViewModelFactory;
import com.cse5236.buckeyeschedule.listeners.ScheduleListener;
import com.cse5236.buckeyeschedule.utilities.Constants;
import com.cse5236.buckeyeschedule.viewmodel.ScheduleViewModel;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ScheduleFragment extends Fragment implements ScheduleListener {

    private FragmentScheduleBinding binding;
    private ScheduleViewModel scheduleViewModel;
    private ScheduleAdapter scheduleAdapter;
    private AlertDialog dialogAddURL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setTitle("My Schedule");
        ((MainActivity) getActivity()).miniIconHelper(true);
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        scheduleViewModel = ViewModelProviders.of(this, new ScheduleViewModelFactory(this.getActivity().getApplication(),
                ((MainActivity) getActivity()).preferenceManager.getString(Constants.KEY_USER_ID))).get(ScheduleViewModel.class);

        //scheduleViewModel.deleteAllSchedule();
        // showToast(Integer.toString(scheduleViewModel.getCountVM()));

        // -> is same as onChange overrides
        scheduleViewModel.getAllSchedules.observe(getViewLifecycleOwner(), schedules -> {
            binding.scheduleRecyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            );
            scheduleAdapter = new ScheduleAdapter(schedules, this);
            binding.scheduleRecyclerView.setAdapter(scheduleAdapter);
            binding.scheduleRecyclerView.smoothScrollToPosition(0);
        });
        setListeners();
        Log.d("fragment lifecycle", "ScheduleFragment onCreateView invoked");
        return v;
    }

    @Override
    public void onScheduleClicked(Schedule schedule, int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("isViewOrUpdate", true);
        bundle.putSerializable("schedule", schedule);
        CreateScheduleFragment createScheduleFragment = new CreateScheduleFragment();
        createScheduleFragment.setArguments(bundle);
        ((MainActivity) getActivity()).replaceFragment(createScheduleFragment, "View Schedule");
    }

    private void setListeners() {
        binding.imageAddScheduleButton.setOnClickListener(v -> {
            ((MainActivity) getActivity()).replaceFragment(new CreateScheduleFragment(), "Create Schedule");
        });
        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                scheduleAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (scheduleViewModel.getCountVM() != 0) {
                    scheduleAdapter.searchSchedule(s.toString());
                }
            }
        });
        binding.imageAddSchedule.setOnClickListener(v -> {
            ((MainActivity) getActivity()).replaceFragment(new CreateScheduleFragment(), "Create Schedule");
        });
        binding.imageTakePhoto.setOnClickListener(v -> {

        });
        binding.imageAddImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                selectImage();
            }
        });
        binding.imageAddURL.setOnClickListener(v -> {
            showAddURLDialog();
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);
    }

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), result -> {
                if (result) {
                    // PERMISSION GRANTED
                    selectImage();
                } else {
                    // PERMISSION NOT GRANTED
                    showToast("Permission Denied!");
                }
            }
    );

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            String selectedImagePath = getPathFromURI(imageUri);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("imagePath", selectedImagePath);
                            CreateScheduleFragment createScheduleFragment = new CreateScheduleFragment();
                            createScheduleFragment.setArguments(bundle);
                            ((MainActivity) getActivity()).replaceFragment(createScheduleFragment, "Create Schedule");
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private String getPathFromURI(Uri contentUri) {
        String filePath;
        Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }

    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.layout_add_url,
                    (ViewGroup) getActivity().findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);
            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null) {
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();
            view.findViewById(R.id.textAdd).setOnClickListener(v -> {
                if (inputURL.getText().toString().trim().isEmpty()) {
                    showToast("Enter URL");
                } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                    showToast("Enter valid URL");
                } else {
                    String url = inputURL.getText().toString();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("URL", url);
                    CreateScheduleFragment createScheduleFragment = new CreateScheduleFragment();
                    createScheduleFragment.setArguments(bundle);
                    ((MainActivity) getActivity()).replaceFragment(createScheduleFragment, "Create Schedule");
                    dialogAddURL.dismiss();
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(v -> {
                dialogAddURL.dismiss();
            });
        }
        dialogAddURL.show();
    }

//    private void getSchedules() {
//        @SuppressLint("StaticFieldLeak")
//        class GetScheduleTask extends AsyncTask<Void, Void, List<Schedule>> {
//
//            @Override
//            protected List<Schedule> doInBackground(Void ...voids) {
//                return ScheduleDatabase.getDatabase(getActivity().getApplicationContext()).scheduleDao().getAllSchedules();
//            }
//            @Override
//            protected void onPostExecute(List<Schedule> schedules) {
//                super.onPostExecute(schedules);
//                Log.d("My Schedules", schedules.toString());
//            }
//        }
//        new GetScheduleTask().execute();
//   }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment lifecycle", "ScheduleFragment onDestroyView invoked");
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}