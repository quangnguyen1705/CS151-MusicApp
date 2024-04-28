package edu.sjsu.musicapp.utilities;

import android.content.Context;

/**
 * Created by fredericknguyen on 04/10/2024
 *
 */

public class ContextProvider {

    private static Context context;

    public ContextProvider (Context context) {
        ContextProvider.context = context;
    }

    public static Context getContext() {
        return context;
    }
}
