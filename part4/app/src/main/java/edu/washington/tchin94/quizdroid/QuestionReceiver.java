package edu.washington.tchin94.quizdroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class QuestionReceiver extends BroadcastReceiver {

    public QuestionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        String url = intent.getStringExtra("url");
        Log.d("DEBUG url received", url);
        Toast.makeText(context, url, Toast.LENGTH_SHORT).show();
    }
}
