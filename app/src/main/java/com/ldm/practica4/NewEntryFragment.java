package com.ldm.practica4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class NewEntryFragment extends Fragment {
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private EditText titleEditText, contentEditText;
    private String imagePath = null, audioPath = null;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private DatePicker entryDatePicker;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch reminderSwitch;
    private DatePicker reminderDatePicker;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch favoriteSwitch;
    private View rootView;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        assert selectedImage != null;
                        imagePath = selectedImage.toString();
                        Toast.makeText(requireContext(), "Imagen seleccionada", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_new_entry, container, false);

        initializeViews();
        setUpListeners();

        return rootView;
    }

    private void initializeViews() {
        titleEditText = rootView.findViewById(R.id.titleEditText);
        contentEditText = rootView.findViewById(R.id.contentEditText);
        entryDatePicker = rootView.findViewById(R.id.entryDatePicker);
        reminderSwitch = rootView.findViewById(R.id.reminderSwitch);
        reminderDatePicker = rootView.findViewById(R.id.reminderDatePicker);
        favoriteSwitch = rootView.findViewById(R.id.favoriteSwitch);
    }

    private void setUpListeners() {
        Button addImageButton = rootView.findViewById(R.id.addImageButton);
        Button recordAudioButton = rootView.findViewById(R.id.recordAudioButton);
        Button saveButton = rootView.findViewById(R.id.saveButton);
        Button backButton = rootView.findViewById(R.id.backButton);

        addImageButton.setOnClickListener(v -> selectImage());
        recordAudioButton.setOnClickListener(v -> {
            checkAudioPermission();
            handleAudioRecording(recordAudioButton);
        });
        saveButton.setOnClickListener(v -> {
            saveEntry();
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            requireActivity().finish();
        });
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            requireActivity().finish();
        });

        reminderSwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                reminderDatePicker.setVisibility(isChecked ? View.VISIBLE : View.GONE));
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    @SuppressLint("SetTextI18n")
    private void handleAudioRecording(Button recordAudioButton) {
        if (!isRecording) {
            try {
                releaseMediaRecorder();
                File audioFile = new File(requireContext().getFilesDir(),
                        "audio_" + System.currentTimeMillis() + ".3gp");
                audioPath = audioFile.getAbsolutePath();

                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setOutputFile(audioPath);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                mediaRecorder.prepare();
                mediaRecorder.start();

                isRecording = true;
                recordAudioButton.setText("Detener Grabación");
                Toast.makeText(requireContext(), "Grabando audio...", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error al iniciar la grabación", Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                mediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error al detener la grabación", Toast.LENGTH_SHORT).show();
            } finally {
                releaseMediaRecorder();
            }
            isRecording = false;
            recordAudioButton.setText("Grabar Audio");
            Toast.makeText(requireContext(), "Audio guardado", Toast.LENGTH_SHORT).show();
        }
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void checkAudioPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    private void saveEntry() {
        Entry entry = new Entry();

        entry.setTitle(titleEditText.getText().toString());
        entry.setContent(contentEditText.getText().toString());
        entry.setImagePath(imagePath);
        entry.setAudioPath(audioPath);
        entry.setTimestamp(System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        calendar.set(entryDatePicker.getYear(),
                entryDatePicker.getMonth(),
                entryDatePicker.getDayOfMonth());
        entry.setEntryDate(calendar.getTimeInMillis());

        entry.setFavorite(favoriteSwitch.isChecked());

        if (reminderSwitch.isChecked()) {
            Calendar reminderCalendar = Calendar.getInstance();
            reminderCalendar.set(reminderDatePicker.getYear(),
                    reminderDatePicker.getMonth(),
                    reminderDatePicker.getDayOfMonth());
            entry.setReminderDate(reminderCalendar.getTimeInMillis());
            scheduleReminder(entry);
        }

        AppDatabase db = Room.databaseBuilder(requireContext(),
                        AppDatabase.class, "diary.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        db.entryDAO().insertEntry(entry);

        Toast.makeText(requireContext(), "Entrada guardada", Toast.LENGTH_SHORT).show();

        // Navegar hacia atrás
        requireActivity().getSupportFragmentManager().popBackStack();
    }

    private void scheduleReminder(Entry entry) {
        Intent intent = new Intent(requireContext(), ReminderReceiver.class);
        intent.putExtra("entryId", entry.getId());
        intent.putExtra("entryTitle", entry.getTitle());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                entry.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, entry.getReminderDate(), pendingIntent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releaseMediaRecorder();
    }
}