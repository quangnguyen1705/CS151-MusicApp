package edu.sjsu.musicapp.config;

import android.app.Application;
import android.preference.PreferenceManager;

import edu.sjsu.musicapp.R;
import edu.sjsu.musicapp.utilities.ContextProvider;
import com.facebook.drawee.backends.pipeline.Fresco;


/**
 * Created by fredericknguyen on 04/10/2024
 *
 */
public class UltraMediaPlayerApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize ContextProvider
        new ContextProvider(getApplicationContext());
        // initialize Fresco image loading
        Fresco.initialize(this);
        // settings
        PreferenceManager.setDefaultValues(this, R.xml.user_settings, false);
    }
}
