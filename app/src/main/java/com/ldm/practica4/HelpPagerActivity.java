package com.ldm.practica4;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class HelpPagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_pager);

        ViewPager2 viewPager = findViewById(R.id.viewPager);
        HelpPagerAdapter adapter = getHelpPagerAdapter();
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {

                }).attach();

        // Configurar botón: Atrás
        findViewById(R.id.backButton).setOnClickListener(v -> {
            MediaPlayerSingleton.getInstance().stop();
            finish();
        });
    }

    @NonNull
    private static HelpPagerAdapter getHelpPagerAdapter() {
        List<String> helpTexts = new ArrayList<>();
        List<Boolean> showTitle = new ArrayList<>();

        // Contenido de las pantallas
        helpTexts.add("Bienvenido a Diarium. Esta aplicación te permite registrar, almacenar y gestionar tus recuerdos de manera sencilla.");
        showTitle.add(false);

        helpTexts.add("- Usa el botón 'Registrar Entrada' para añadir una nueva entrada al diario.");
        showTitle.add(true);

        helpTexts.add("- Usa el botón 'Ver Entradas' para consultar todas las entradas guardadas.");
        showTitle.add(true);

        helpTexts.add("- Usa el botón 'Favoritos' para consultar las entradas favoritas.");
        showTitle.add(true);

        helpTexts.add("- Usa el botón 'Calendario' para consultar un calendario mensual y las entradas que tienes cada día.");
        showTitle.add(true);

        helpTexts.add("- Puedes eliminar entradas desde la lista, si es necesario.");
        showTitle.add(true);

        helpTexts.add("- Puedes añadir imágenes/audio a tus entradas.");
        showTitle.add(true);

        helpTexts.add("- Puedes poner recordatorios.");
        showTitle.add(true);

        helpTexts.add("- Y muchas cosas más...");
        showTitle.add(true);

        // Configurar el adaptador
        return new HelpPagerAdapter(helpTexts, showTitle);
    }
}