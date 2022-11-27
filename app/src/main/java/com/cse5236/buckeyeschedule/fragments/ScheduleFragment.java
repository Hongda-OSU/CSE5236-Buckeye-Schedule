package com.cse5236.buckeyeschedule.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Environment;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleFragment extends Fragment implements ScheduleListener {

    private FragmentScheduleBinding binding;
    private ScheduleViewModel scheduleViewModel;
    private ScheduleAdapter scheduleAdapter;
    private AlertDialog dialogAddURL;
    private String latestImageTaken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (Constants.LANGUAGE == "zh"){
            ((MainActivity) getActivity()).setTitle("我的计划");
        } else {
            ((MainActivity) getActivity()).setTitle("My Schedule");
        }
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
            boolean isCameraPermissionGranted;
            boolean isImageSavingPermissionGranted;
            isCameraPermissionGranted = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            isImageSavingPermissionGranted = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            List<String> permissionRequest = new ArrayList<>();
            if (!isCameraPermissionGranted) {
                permissionRequest.add(Manifest.permission.CAMERA);
            }
            if (!isImageSavingPermissionGranted) {
                permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissionRequest.isEmpty()) {
                requestCameraActionPermissionLauncher.launch(permissionRequest.toArray(new String[0]));
            } else {
                takePhoto();
            }
        });
        binding.imageAddImage.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPhotoPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
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
        pickImageLauncher.launch(intent);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            showToast("Cannot create image file.");
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(getActivity().getApplicationContext(),
                    "com.cse5236.buckeyeschedule.fileprovider",
                    photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            takePhotoLauncher.launch(intent);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
        String imageFileName = "Image_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        latestImageTaken = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(latestImageTaken);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private ActivityResultLauncher<String> requestPhotoPermissionLauncher = registerForActivityResult(
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

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            String selectedImagePath = getPathFromURI(imageUri);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("imagePath", selectedImagePath);
                            CreateScheduleFragment createScheduleFragment = new CreateScheduleFragment();
                            createScheduleFragment.setArguments(bundle);
                            ((MainActivity) getActivity()).replaceFragment(createScheduleFragment, "Create Schedule");
                        } catch (Exception e) {
                            showToast(e.getMessage());
                        }
                    }
                }
            }
    );

    private ActivityResultLauncher<String[]> requestCameraActionPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                if (result.get(Manifest.permission.CAMERA) && result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    takePhoto();
                } else {
                    showToast("Permission Denied!");
                }
            }
    );

    private final ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    galleryAddPic();
                    if (latestImageTaken != null) {
                        try {
                            ExifInterface ei = new ExifInterface(latestImageTaken);
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);
                            int rotation;
                            switch(orientation) {
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotation = 90;
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotation = 180;
                                    break;

                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotation = 270;
                                    break;

                                case ExifInterface.ORIENTATION_NORMAL:
                                default:
                                    rotation = 0;
                            }
                            //latestImageTaken = getLatestImageTaken();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("latestImageTaken", latestImageTaken);
                            bundle.putSerializable("rotation", rotation);
                            CreateScheduleFragment createScheduleFragment = new CreateScheduleFragment();
                            createScheduleFragment.setArguments(bundle);
                            ((MainActivity) getActivity()).replaceFragment(createScheduleFragment, "Create Schedule");
                        } catch (Exception e) {
                            showToast(e.getMessage());
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

    private String getLatestImageTaken() {
        String filePath;
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        Cursor cursor = getContext().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

        cursor.moveToFirst();
        filePath = cursor.getString(1);
        return filePath;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment lifecycle", "ScheduleFragment onDestroyView invoked");
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}