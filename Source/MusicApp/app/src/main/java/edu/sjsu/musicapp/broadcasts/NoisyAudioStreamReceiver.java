package edu.sjsu.musicapp.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import edu.sjsu.musicapp.services.MusicService;

/**
 * Created by fredericknguyen on 04/10/2024
 *
 */

public class NoisyAudioStreamReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            // Pause the playback
            if (MusicService.musicService.isPlaying()) {
                MusicService.musicService.toggleState();
            }
        }
    }
}
