package edu.sjsu.musicapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import edu.sjsu.musicapp.R;
import edu.sjsu.musicapp.services.MusicService;
import edu.sjsu.musicapp.services.VideoService;
import edu.sjsu.musicapp.utilities.Constants;
import edu.sjsu.musicapp.utilities.UtilFunctions;
import com.victor.loading.newton.NewtonCradleLoading;

/**
 * Created by fredericknguyen on 04/10/2024
 *
 */

public class LauncherActivity extends Activity {

    NewtonCradleLoading newtonCradleLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        newtonCradleLoading = (NewtonCradleLoading) findViewById(R.id.newton_cradle_loading);
        newtonCradleLoading.start();

        if (Build.VERSION.SDK_INT >= 22) {
            askPermission();
        } else {
            goToMainActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goToMainActivity();
                } else {
                    Toast.makeText(LauncherActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                    newtonCradleLoading.stop();
                }
            }
        }
    }

    private void askPermission() {
        // ask for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            goToMainActivity();
        }
    }

    private void goToMainActivity() {
        boolean isMusicServiceRunning = UtilFunctions.isServiceRunning(MusicService.class.getName(), getApplicationContext());
        boolean isVideoServiceRunning = UtilFunctions.isServiceRunning(VideoService.class.getName(), getApplicationContext());
        if ((!isMusicServiceRunning || (MusicService.musicService == null)) && (!isVideoServiceRunning || (VideoService.videoService == null))) {
            Intent musicServiceIntent = new Intent(getApplicationContext(), MusicService.class);
            startService(musicServiceIntent);
            Intent videoServiceIntent = new Intent(getApplicationContext(), VideoService.class);
            startService(videoServiceIntent);
            // pause for a while to get the service running before moving to main screen...
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(mainActivityIntent);
                    finish();
                }
            }, 3000);
        } else {
            // service is already running, move at once
            Intent mainMusicScreenIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainMusicScreenIntent);
            finish();
        }
    }
}
