package edu.sjsu.musicapp.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.sjsu.musicapp.services.MusicService;
import edu.sjsu.musicapp.services.VideoService;

/**
 * Created by fredericknguyen on 04/10/2024
 *
 */
public class MediaServiceStartBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        startMusicService(context);
        startVideoService(context);
    }

    private void startMusicService(Context context) {
        Intent musicServiceIntent = new Intent(context, MusicService.class);
        context.startService(musicServiceIntent);
    }

    private void startVideoService(Context context) {
        Intent videoServiceIntent = new Intent(context, VideoService.class);
        context.startService(videoServiceIntent);
    }
}
