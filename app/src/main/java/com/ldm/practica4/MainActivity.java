package com.ldm.practica4;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {
    private LinearLayout mainButtonsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainButtonsContainer = findViewById(R.id.mainButtonsContainer);

        Button registerEntryButton = findViewById(R.id.registerEntryButton);
        Button viewEntriesButton = findViewById(R.id.viewEntriesButton);
        Button favoritesButton = findViewById(R.id.favoritesButton);
        Button calendarButton = findViewById(R.id.calendarButton);

        registerEntryButton.setOnClickListener(v -> openFragment(new NewEntryFragment()));
        viewEntriesButton.setOnClickListener(v ->
        {
            ViewEntriesFragment fragment = new ViewEntriesFragment();
            Bundle args = new Bundle();
            args.putBoolean("showFavorites", false);
            fragment.setArguments(args);
            openFragment(fragment);
        });
        favoritesButton.setOnClickListener(v -> {
            ViewEntriesFragment fragment = new ViewEntriesFragment();
            Bundle args = new Bundle();
            args.putBoolean("showFavorites", true);
            fragment.setArguments(args);
            openFragment(fragment);
        });
        calendarButton.setOnClickListener(v -> openFragment(new CalendarFragment()));

        // Botón: Ayuda
        findViewById(R.id.helpButton).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HelpPagerActivity.class);
            startActivity(intent);
            MediaPlayerSingleton.getInstance().initialize(this, R.raw.help);
            MediaPlayerSingleton.getInstance().play();
        });
    }

    private void openFragment(Fragment fragment) {
        // Ocultar botones
        mainButtonsContainer.setVisibility(View.GONE);

        // Cargar el fragmento
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}