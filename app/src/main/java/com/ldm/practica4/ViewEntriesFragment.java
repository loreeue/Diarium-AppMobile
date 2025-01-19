package com.ldm.practica4;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import java.util.List;
import android.Manifest;
import android.widget.Button;

public class ViewEntriesFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private RecyclerView recyclerView;
    private EntryAdapter adapter;
    private boolean showFavorites;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_view_entries, container, false);

        // Verificar y solicitar permisos
        checkAndRequestPermissions();

        recyclerView = rootView.findViewById(R.id.entryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        AppDatabase db = Room.databaseBuilder(requireContext(),
                        AppDatabase.class, "diary.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        List<Entry> entries = db.entryDAO().getAllEntries();

        adapter = new EntryAdapter(entries);
        recyclerView.setAdapter(adapter);

        if (getArguments() != null) {
            showFavorites = getArguments().getBoolean("showFavorites", false);
        }

        SearchView searchView = rootView.findViewById(R.id.searchView);

        if (showFavorites) {
            searchView.setVisibility(View.GONE);
        } else {
            searchView.setVisibility(View.VISIBLE);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    performSearch(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    performSearch(newText);
                    return true;
                }
            });
        }
        loadEntries();

        Button backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            requireActivity().finish();
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (adapter != null) {
            adapter.releaseMediaPlayer();
        }
    }

    private void loadEntries() {
        AppDatabase db = Room.databaseBuilder(requireContext(),
                        AppDatabase.class, "diary.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        List<Entry> entries;

        if (showFavorites) {
            entries = db.entryDAO().getFavoriteEntries();
        } else {
            entries = db.entryDAO().getAllEntries();
        }

        adapter = new EntryAdapter(entries);
        recyclerView.setAdapter(adapter);
    }

    private void performSearch(String query) {
        AppDatabase db = Room.databaseBuilder(requireContext(),
                        AppDatabase.class, "diary.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        List<Entry> searchResults = db.entryDAO().searchEntries(query);
        adapter = new EntryAdapter(searchResults);
        recyclerView.setAdapter(adapter);
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            } else {
                initializeRecyclerView();
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                initializeRecyclerView();
            }
        }
    }

    private void initializeRecyclerView() {
        recyclerView = rootView.findViewById(R.id.entryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        AppDatabase db = Room.databaseBuilder(requireContext(),
                        AppDatabase.class, "diary.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        List<Entry> entries = db.entryDAO().getAllEntries();

        adapter = new EntryAdapter(entries);
        recyclerView.setAdapter(adapter);
    }
}