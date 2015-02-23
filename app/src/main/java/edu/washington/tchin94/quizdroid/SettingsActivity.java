package edu.washington.tchin94.quizdroid;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;


import java.util.List;

public class SettingsActivity extends PreferenceActivity {
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private static final int TIME_UNIT = 1000; //minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupSimplePreferencesScreen();

        EditTextPreference urlPref = (EditTextPreference) findPreference("url_text");
        final EditTextPreference timeIntervalPref = (EditTextPreference) findPreference("time_interval");

        urlPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();
                Log.d("DEBUG new url", stringValue);
                preference.setSummary(stringValue);
                long time = Integer.parseInt(timeIntervalPref.getText()) * TIME_UNIT;
                Log.d("DEBUG time", time + "");
                String url = stringValue;
                setAlarm(SettingsActivity.this, 0, time, url);
                return true;
            }
        });

        timeIntervalPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();
                preference.setSummary(stringValue);
                long time = Integer.parseInt(stringValue) * TIME_UNIT;
                Log.d("DEBUG time", time + "");
                String url = preference.getSharedPreferences().getString("url_text", "www.fake.com");
                setAlarm(SettingsActivity.this, 0 ,time, url);
                return true;
            }
        });
    }


    public void setAlarm(Context mContext, int requestCode, long time, String url){
        Log.d("DEBUG setting new alarm", "true");
        cancelAlarmIfExists(mContext,requestCode);

        Intent myIntent = new Intent(mContext, QuestionReceiver.class);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("DEBUG new url", url);
        myIntent.putExtra("url", url);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(SettingsActivity.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager)mContext.getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis() + time), time, pendingIntent);
    }

    public void cancelAlarmIfExists(Context mContext,int requestCode){
        try{
            Intent myIntent = new Intent(mContext, QuestionReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, requestCode, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am=(AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.d("DEBUG removing old alarm", "true");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(findPreference("url_text"));
        bindPreferenceSummaryToValue(findPreference("time_interval"));
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);

            }
            return true;
        }
    };

    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            bindPreferenceSummaryToValue(findPreference("url_text"));
            bindPreferenceSummaryToValue(findPreference("time_interval"));
        }
    }

}
