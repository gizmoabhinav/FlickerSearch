package com.abhinav.imagesearcher.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Utility class for static network functions
 */
public class NetworkUtils {

    /**
     * This function returns if the device has active network connection
     * @param context Activity context
     * @return true if connected, false otherwise
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
