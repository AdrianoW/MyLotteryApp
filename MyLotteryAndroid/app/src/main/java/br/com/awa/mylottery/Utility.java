package br.com.awa.mylottery;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by adrianowalmeida on 20/12/15.
 */
public class Utility {
    public static boolean getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_logged_key), true);
    }

}
