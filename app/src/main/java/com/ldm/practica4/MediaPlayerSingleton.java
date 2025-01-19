package com.ldm.practica4;

import android.content.Context;
import android.media.MediaPlayer;

public class MediaPlayerSingleton {
    private static MediaPlayerSingleton instance;
    private MediaPlayer mediaPlayer;

    private MediaPlayerSingleton() {}

    public static MediaPlayerSingleton getInstance() {
        if (instance == null) {
            instance = new MediaPlayerSingleton();
        }
        return instance;
    }

    public void initialize(Context context, int audioResId) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, audioResId);
        }
    }

    public void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}