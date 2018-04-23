package com.alli.shoppinglist.utils;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by omo_lanke on 22/04/2018.
 */

public class Utils {
    public static String getUserId(Context context){
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
    public static long getCurrentDate(){
        return System.currentTimeMillis();
    }
}
