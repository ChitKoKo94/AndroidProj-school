package com.example.android_ca;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {
    MediaPlayer medPlayer;
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void onCreate()
    {
        medPlayer = MediaPlayer.create(this, R.raw.game_music);
        medPlayer.start();
        medPlayer.setLooping(true);

    }
    public void onForeGroundService(Intent intent, int startId)
    {
    }
    public void onDestroy()
    {
        medPlayer.stop();
    }




}