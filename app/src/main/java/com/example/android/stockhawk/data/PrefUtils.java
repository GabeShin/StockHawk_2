package com.example.android.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.stockhawk.R;
import com.example.android.stockhawk.sync.QuoteSyncJob;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Gabe on 2016-11-24.
 */
public final class PrefUtils {

    private PrefUtils() {
    }

    public static Set<String> getStocks(Context context) {
        String stocksKey = context.getString(R.string.pref_stocks_key);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (isFirstRun(context)) {
            String[] defaultStocksList = context.getResources().getStringArray(R.array.default_stocks);

            HashSet<String> defaultStocks = new HashSet<>(Arrays.asList(defaultStocksList));

            SharedPreferences.Editor editor = prefs.edit();
            editor.putStringSet(stocksKey, defaultStocks);
            editor.apply();
            return defaultStocks;
        }

        return prefs.getStringSet(stocksKey, new HashSet<String>());
    }

    public static boolean isFirstRun(Context context){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (pref.getBoolean(context.getString(R.string.pref_stocks_initialized_key), true)){
            pref.edit().putBoolean(context.getString(R.string.pref_stocks_initialized_key), false).apply();
            return true;
        }
        return false;
    }

    public static void editStockPref(Context context, String symbol, Boolean add) {
        String key = context.getString(R.string.pref_stocks_key);
        Set<String> stocks = getStocks(context);

        if (add) {
            stocks.add(symbol);
        } else {
            stocks.remove(symbol);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, stocks);
        editor.apply();
    }

    public static void addStock(Context context, String symbol) {
        editStockPref(context, symbol, true);
    }

    public static void removeStock(Context context, String symbol) {
        editStockPref(context, symbol, false);
    }

    public static String getDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static void toggleDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String absoluteKey = context.getString(R.string.pref_display_mode_absolute_key);
        String percentageKey = context.getString(R.string.pref_display_mode_percentage_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String displayMode = getDisplayMode(context);

        SharedPreferences.Editor editor = prefs.edit();

        if (displayMode.equals(absoluteKey)) {
            editor.putString(key, percentageKey);
        } else {
            editor.putString(key, absoluteKey);
        }

        editor.apply();
    }

    /**
     *
     * @param c Context used to get the SharedPreferences
     * @return the SYNC status integer type
     */
    @SuppressWarnings("ResourceType")
    static public @QuoteSyncJob.SyncStatus
    int getSyncStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_sync_status), QuoteSyncJob.STATUS_OK);
    }

    /**
     * Resets the SYNC status.  (Sets it to SunshineSyncAdapter.LOCATION_STATUS_UNKNOWN)
     * @param c Context used to get the SharedPreferences
     */
    static public void resetSyncStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_sync_status), QuoteSyncJob.STATUS_OK);
        spe.apply();
    }
}
