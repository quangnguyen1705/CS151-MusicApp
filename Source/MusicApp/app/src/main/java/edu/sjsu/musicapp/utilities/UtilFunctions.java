package edu.sjsu.musicapp.utilities;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

/**
 * Created by fredericknguyen on 04/10/2024
 *
 */

public class UtilFunctions {

    public static boolean isServiceRunning(String serviceName, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAndroidM() {
        return "MNC".equals(Build.VERSION.CODENAME);
    }
}
