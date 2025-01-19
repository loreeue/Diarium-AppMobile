package com.ldm.practica4;

import android.app.AlertDialog;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import java.io.File;
import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<EntryAdapter.EntryViewHolder> {
    private final List<Entry> entries;
    private MediaPlayer mediaPlayer;

    public EntryAdapter(List<Entry> entries) {
        this.entries = entries;
    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        Entry entry = entries.get(position);

        holder.titleTextView.setText(entry.getTitle());
        holder.contentTextView.setText(entry.getContent());

        // Manejo de la imagen
        if (entry.getImagePath() != null && !entry.getImagePath().isEmpty()) {
            holder.viewImageButton.setVisibility(View.VISIBLE);
            holder.viewImageButton.setOnClickListener(v -> {
                Dialog dialog = new Dialog(holder.itemView.getContext());
                dialog.setContentView(R.layout.dialog_view_image);
                ImageView imageView = dialog.findViewById(R.id.imageView);

                Uri imageUri = Uri.parse(entry.getImagePath());
                imageView.setImageURI(imageUri);

                dialog.show();
            });
        } else {
            holder.viewImageButton.setVisibility(View.GONE);
        }

        // Manejo del audio
        if (entry.getAudioPath() != null && !entry.getAudioPath().isEmpty()) {
            holder.playAudioButton.setVisibility(View.VISIBLE);
            holder.playAudioButton.setOnClickListener(v -> {
                try {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            Log.d("AudioPlayback", "Audio detenido");
                        }
                        mediaPlayer.release();
                        mediaPlayer = null;
                        holder.playAudioButton.setImageResource(R.drawable.ic_audio);
                    } else {
                        Log.d("AudioPlayback", "Intentando reproducir archivo: " + entry.getAudioPath());
                        mediaPlayer = new MediaPlayer();

                        // Verificar si el archivo existe
                        File audioFile = new File(entry.getAudioPath());
                        if (!audioFile.exists()) {
                            Toast.makeText(holder.itemView.getContext(),
                                    "El archivo de audio no existe", Toast.LENGTH_SHORT).show();
                            Log.e("AudioPlayback", "El archivo no existe: " + entry.getAudioPath());
                            return;
                        }

                        mediaPlayer.setDataSource(entry.getAudioPath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Log.d("AudioPlayback", "Audio iniciado");
                        holder.playAudioButton.setImageResource(R.drawable.ic_stop);

                        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                            Log.e("AudioPlayback", "Error en MediaPlayer: " + what + ", " + extra);
                            Toast.makeText(holder.itemView.getContext(),
                                    "Error al reproducir el audio", Toast.LENGTH_SHORT).show();
                            return false;
                        });

                        mediaPlayer.setOnCompletionListener(mp -> {
                            Log.d("AudioPlayback", "Audio completado");
                            holder.playAudioButton.setImageResource(R.drawable.ic_audio);
                            mediaPlayer.release();
                            mediaPlayer = null;
                        });
                    }
                } catch (Exception e) {
                    Log.e("AudioPlayback", "Error al reproducir audio", e);
                    Toast.makeText(holder.itemView.getContext(),
                            "Error al reproducir el audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
            });
        } else {
            Log.d("AudioPlayback", "No hay ruta de audio para esta entrada");
            holder.playAudioButton.setVisibility(View.GONE);
        }

        // Manejo de eliminar entrada
        holder.deleteEntryButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
            builder.setMessage("¿Estás seguro de que quieres eliminar esta entrada?")
                    .setPositiveButton("Sí", (dialog, id) -> {
                        AppDatabase db = Room.databaseBuilder(holder.itemView.getContext(),
                                AppDatabase.class, "diary.db").allowMainThreadQueries().build();
                        db.entryDAO().deleteEntry(entry);
                        entries.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, entries.size());
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public static class EntryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView;
        ImageButton viewImageButton;
        ImageButton playAudioButton;
        ImageButton deleteEntryButton;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            viewImageButton = itemView.findViewById(R.id.viewImageButton);
            playAudioButton = itemView.findViewById(R.id.playAudioButton);
            deleteEntryButton = itemView.findViewById(R.id.deleteEntryButton);
        }
    }
}