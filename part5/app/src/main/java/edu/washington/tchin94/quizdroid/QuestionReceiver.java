package edu.washington.tchin94.quizdroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.logging.Handler;

public class QuestionReceiver extends BroadcastReceiver {

    public QuestionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
//        String url = intent.getStringExtra("url");
        Intent mServiceIntent = new Intent(context, DownloadService.class);
        context.startService(mServiceIntent);
    }
}
