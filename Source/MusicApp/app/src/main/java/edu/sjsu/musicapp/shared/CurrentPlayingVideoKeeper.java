package edu.sjsu.musicapp.shared;

import edu.sjsu.musicapp.models.Video;

/**
 * Created by fredericknguyen on 04/10/2024
 *
 */

public class CurrentPlayingVideoKeeper {

    public static Video currentPlayingVideo;

    public static void setCurrentPlayingVideo(Video video) {
        currentPlayingVideo = video;
    }

    public static Video getCurrentPlayingVideo() {
        return currentPlayingVideo;
    }

    public static void resetCurrentPlayingVideo() {
        currentPlayingVideo = null;
    }
}
