package com.ldm.practica4;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {
    private RecyclerView entriesRecyclerView;
    private EntryAdapter adapter;
    private AppDatabase db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        entriesRecyclerView = view.findViewById(R.id.entriesRecyclerView);
        entriesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        Button backButton = view.findViewById(R.id.backButton);

        db = Room.databaseBuilder(requireContext(),
                        AppDatabase.class, "diary.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            requireActivity().finish();
        });

        calendarView.setOnDateChangeListener((calendarView1, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            List<Entry> entries = db.entryDAO().getEntriesByDate(calendar.getTimeInMillis());
            adapter = new EntryAdapter(entries);
            entriesRecyclerView.setAdapter(adapter);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.releaseMediaPlayer();
        }
    }
}