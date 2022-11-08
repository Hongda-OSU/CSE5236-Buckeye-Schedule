package com.cse5236.buckeyeschedule.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cse5236.buckeyeschedule.R;
import com.cse5236.buckeyeschedule.activities.MainActivity;
import com.cse5236.buckeyeschedule.databinding.FragmentCreateScheduleBinding;
import com.cse5236.buckeyeschedule.entities.Schedule;
import com.cse5236.buckeyeschedule.factory.ScheduleViewModelFactory;
import com.cse5236.buckeyeschedule.utilities.Constants;
import com.cse5236.buckeyeschedule.viewmodel.ScheduleViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateScheduleFragment extends Fragment {

    private FragmentCreateScheduleBinding binding;
    private ScheduleViewModel scheduleViewModel;
    private String selectedScheduleColor = "#333333";
    private String selectedImagePath = "";
    private AlertDialog dialogAddURL;
    private AlertDialog dialogDeleteSchedule;
    private Schedule availableSchedule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).setTitle("Create Schedule");
        ((MainActivity) getActivity()).miniIconHelper(false);
        binding = FragmentCreateScheduleBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        scheduleViewModel = ViewModelProviders.of(this, new ScheduleViewModelFactory(this.getActivity().getApplication(), ((MainActivity) getActivity()).preferenceManager.getString(Constants.KEY_USER_ID))).get(ScheduleViewModel.class);
        setTime();
        setListeners();
        setSubtitleIndicatorColor();
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            availableSchedule = (Schedule) bundle.getSerializable("schedule");
            if (availableSchedule != null) {
                setViewOrUpdateSchedule();
            } else {
                setLatestImageSchedule();
            }
        }
        initMiscellaneous();
        Log.d("fragment lifecycle", "CreateScheduleFragment onCreateView invoked");
        return v;
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> {
            ((MainActivity) getActivity()).onBackPressed();
        });
        binding.imageSave.setOnClickListener(v -> saveSchedule());
        binding.imageRemoveWebURL.setOnClickListener(v -> {
            binding.textWebURL.setText(null);
            binding.layoutWebURL.setVisibility(View.GONE);
        });
        binding.imageRemoveImage.setOnClickListener(v -> {
            binding.imageSchedule.setImageBitmap(null);
            binding.imageSchedule.setVisibility(View.GONE);
            binding.imageRemoveImage.setVisibility(View.GONE);
            selectedImagePath = "";
        });
    }

    private void setTime() {
        binding.textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault())
                        .format(new Date())
        );
    }

    private void setViewOrUpdateSchedule() {
        binding.inputScheduleTitle.setText(availableSchedule.getScheduleTitle());
        binding.inputScheduleSubtitle.setText(availableSchedule.getScheduleSubtitle());
        binding.inputSchedule.setText(availableSchedule.getScheduleDescription());
        binding.textDateTime.setText(availableSchedule.getDateTime());
        if (availableSchedule.getImagePath() != null && !availableSchedule.getImagePath().trim().isEmpty()) {
            binding.imageSchedule.setImageBitmap(BitmapFactory.decodeFile(availableSchedule.getImagePath()));
            binding.imageSchedule.setVisibility(View.VISIBLE);
            binding.imageRemoveImage.setVisibility(View.VISIBLE);
            selectedImagePath = availableSchedule.getImagePath();
        }
        if (availableSchedule.getWebLink() != null && !availableSchedule.getWebLink().trim().isEmpty()) {
            binding.textWebURL.setText(availableSchedule.getWebLink());
            binding.layoutWebURL.setVisibility(View.VISIBLE);
        }
    }

    private void saveSchedule() {
        if (binding.inputScheduleTitle.getText().toString().trim().isEmpty()) {
            showToast("Schedule title can't be empty");
            return;
        } else if (binding.inputScheduleSubtitle.getText().toString().trim().isEmpty()) {
            showToast("Schedule subtitle can't be empty");
            return;
        } else if (binding.inputSchedule.getText().toString().trim().isEmpty()) {
            showToast("Schedule description can't be empty");
            return;
        }

        final Schedule schedule = new Schedule();
        schedule.setScheduleTitle(binding.inputScheduleTitle.getText().toString());
        schedule.setScheduleSubtitle(binding.inputScheduleSubtitle.getText().toString());
        schedule.setScheduleDescription(binding.inputSchedule.getText().toString());
        schedule.setDateTime(binding.textDateTime.getText().toString());
        schedule.setUserId(((MainActivity) getActivity()).preferenceManager.getString(Constants.KEY_USER_ID));
        // need change
        schedule.setCategory(selectedScheduleColor);
        schedule.setImagePath(selectedImagePath);

        if (binding.layoutWebURL.getVisibility() == View.VISIBLE) {
            schedule.setWebLink(binding.textWebURL.getText().toString());
        }

        if (availableSchedule != null) {
            schedule.setScheduleId(availableSchedule.getScheduleId());
            schedule.setUserId(availableSchedule.getUserId());
        }

        scheduleViewModel.insertSchedule(schedule);

        ((MainActivity) getActivity()).replaceFragment(new ScheduleFragment(), "Schedule");

//        class SaveScheduleTask extends AsyncTask<Void, Void, Void> {
//            @Override
//            protected Void doInBackground(Void ... voids) {
//                ScheduleDatabase.getDatabase(getActivity().getApplicationContext()).scheduleDao().insertSchedule(schedule);
//                return null;
//            }
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                ((MainActivity) getActivity()).replaceFragment(new ScheduleFragment(), "Schedule");
//            }
//        }
//        new SaveScheduleTask().execute();
    }

    private void initMiscellaneous() {
        final LinearLayout layoutMiscellaneous = binding.layoutMiscellaneous.getRoot();
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        binding.layoutMiscellaneous.viewColor1.setOnClickListener(v -> {
            selectedScheduleColor = "#333333";
            binding.layoutMiscellaneous.imageColor1.setImageResource(R.drawable.ic_baseline_done_24);
            binding.layoutMiscellaneous.imageColor2.setImageResource(0);
            binding.layoutMiscellaneous.imageColor3.setImageResource(0);
            binding.layoutMiscellaneous.imageColor4.setImageResource(0);
            binding.layoutMiscellaneous.imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        binding.layoutMiscellaneous.viewColor2.setOnClickListener(v -> {
            selectedScheduleColor = "#FDBE3B";
            binding.layoutMiscellaneous.imageColor1.setImageResource(0);
            binding.layoutMiscellaneous.imageColor2.setImageResource(R.drawable.ic_baseline_done_24);
            binding.layoutMiscellaneous.imageColor3.setImageResource(0);
            binding.layoutMiscellaneous.imageColor4.setImageResource(0);
            binding.layoutMiscellaneous.imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        binding.layoutMiscellaneous.viewColor3.setOnClickListener(v -> {
            selectedScheduleColor = "#FF4842";
            binding.layoutMiscellaneous.imageColor1.setImageResource(0);
            binding.layoutMiscellaneous.imageColor2.setImageResource(0);
            binding.layoutMiscellaneous.imageColor3.setImageResource(R.drawable.ic_baseline_done_24);
            binding.layoutMiscellaneous.imageColor4.setImageResource(0);
            binding.layoutMiscellaneous.imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        binding.layoutMiscellaneous.viewColor4.setOnClickListener(v -> {
            selectedScheduleColor = "#3A52FC";
            binding.layoutMiscellaneous.imageColor1.setImageResource(0);
            binding.layoutMiscellaneous.imageColor2.setImageResource(0);
            binding.layoutMiscellaneous.imageColor3.setImageResource(0);
            binding.layoutMiscellaneous.imageColor4.setImageResource(R.drawable.ic_baseline_done_24);
            binding.layoutMiscellaneous.imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });

        binding.layoutMiscellaneous.viewColor5.setOnClickListener(v -> {
            selectedScheduleColor = "#000000";
            binding.layoutMiscellaneous.imageColor1.setImageResource(0);
            binding.layoutMiscellaneous.imageColor2.setImageResource(0);
            binding.layoutMiscellaneous.imageColor3.setImageResource(0);
            binding.layoutMiscellaneous.imageColor4.setImageResource(0);
            binding.layoutMiscellaneous.imageColor5.setImageResource(R.drawable.ic_baseline_done_24);
            setSubtitleIndicatorColor();
        });

        binding.layoutMiscellaneous.layoutAddImage.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                selectImage();
            }
        });

        binding.layoutMiscellaneous.layoutAddUrl.setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            showAddURLDialog();
        });

        if (availableSchedule != null) {
            layoutMiscellaneous.findViewById(R.id.layoutDeleteSchedule).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutDeleteSchedule).setOnClickListener(v -> {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showDeleteScheduleDialog();
            });
        }

        if (availableSchedule != null && availableSchedule.getCategory() != null && !availableSchedule.getCategory().trim().isEmpty()) {
            switch (availableSchedule.getCategory()) {
                case "#FDBE3B":
                    layoutMiscellaneous.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#FF4842":
                    layoutMiscellaneous.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#3A52FC":
                    layoutMiscellaneous.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#000000":
                    layoutMiscellaneous.findViewById(R.id.viewColor5).performClick();
                    break;
                default:
                    layoutMiscellaneous.findViewById(R.id.viewColor1).performClick();
                    break;
            }
        }
    }

    private void showDeleteScheduleDialog() {
        if (dialogDeleteSchedule == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.layout_delete_schedule,
                    (ViewGroup) getActivity().findViewById(R.id.layoutDeleteScheduleContainer)
            );
            builder.setView(view);
            dialogDeleteSchedule = builder.create();
            if (dialogDeleteSchedule.getWindow() != null) {
                dialogDeleteSchedule.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteSchedule).setOnClickListener(v -> {
                scheduleViewModel.deleteSchedule(availableSchedule.getScheduleId());
                dialogDeleteSchedule.dismiss();
                ((MainActivity) getActivity()).replaceFragment(new ScheduleFragment(), "Schedule");
            });
            view.findViewById(R.id.textCancel).setOnClickListener(v -> {
                dialogDeleteSchedule.dismiss();
            });
        }
        dialogDeleteSchedule.show();
    }

    private void setSubtitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) binding.viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedScheduleColor));
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
                            binding.imageSchedule.setImageBitmap(bitmap);
                            binding.imageSchedule.setVisibility(View.VISIBLE);
                            selectedImagePath = getPathFromURI(imageUri);
                            binding.imageRemoveImage.setVisibility(View.VISIBLE);
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

    private void setLatestImageSchedule() {
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
        // Bitmap bm = BitmapFactory.decodeFile(filePath);
        // binding.imageSchedule.setImageBitmap(bm);
        selectedImagePath = filePath;
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
                    binding.textWebURL.setText(inputURL.getText().toString());
                    binding.layoutWebURL.setVisibility(View.VISIBLE);
                    dialogAddURL.dismiss();
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(v -> {
                dialogAddURL.dismiss();
            });
        }
        dialogAddURL.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("fragment lifecycle", "CreateScheduleFragment onDestroyView invoked");
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}