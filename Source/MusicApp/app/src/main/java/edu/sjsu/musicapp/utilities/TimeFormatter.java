package edu.sjsu.musicapp.utilities;

/**
 * Created by fredericknguyen on 04/10/2024
 *
 */

public class TimeFormatter {

    public static String getTimeString(long millis) {

        int minutes = (int) (millis % (1000 * 60 * 60)) / (1000*60);
        int seconds = (int) ((millis % (1000 * 60 * 60)) % (1000 * 60) ) / 1000;

        return String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }
}
