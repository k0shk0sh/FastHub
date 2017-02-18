package com.fastaccess.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by kosh20111 on 18 Oct 2016, 9:29 PM
 */

public class AppHelper {

    @SuppressWarnings("deprecation") public static boolean isOnline(@NonNull Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (isM()) {
                Network networks = cm.getActiveNetwork();
                NetworkInfo netInfo = cm.getNetworkInfo(networks);
                haveConnectedWifi = netInfo.getType() == ConnectivityManager.TYPE_WIFI && netInfo.getState().equals(NetworkInfo.State.CONNECTED);
                haveConnectedMobile = netInfo.getType() == ConnectivityManager.TYPE_MOBILE && netInfo.getState().equals(NetworkInfo.State.CONNECTED);
            } else {
                NetworkInfo[] netInfo = cm.getAllNetworkInfo();
                for (NetworkInfo ni : netInfo) {
                    if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                        if (ni.isConnected())
                            haveConnectedWifi = true;
                    }
                    if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                        if (ni.isConnected())
                            haveConnectedMobile = true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @SuppressWarnings("WeakerAccess") public static boolean isM() {return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;}

    public static void hideKeyboard(@NonNull View view) {
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Nullable public static Fragment getFragmentByTag(@NonNull FragmentManager fragmentManager, @NonNull String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }
}
