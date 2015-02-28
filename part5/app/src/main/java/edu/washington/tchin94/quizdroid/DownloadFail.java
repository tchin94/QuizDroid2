package edu.washington.tchin94.quizdroid;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class DownloadFail extends ActionBarActivity {

    private static final int TIME_UNIT = 1000 * 60; //minutes
    private static final int DEFAULT_TIME_INTERVAL = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cancelAlarmIfExists(this, 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.download_error_message)
                .setTitle(R.string.download_error_title);

        builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setQuestionDownload();
                finish();
            }
        });
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //sets periodic question download request
    public void setQuestionDownload() {
        Log.d("set question fetching", "DEBUG");
        Intent questionIntent = new Intent(this, QuestionReceiver.class);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int interval = Integer.parseInt(pref.getString("time_interval", DEFAULT_TIME_INTERVAL + "")) * TIME_UNIT;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, questionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()), interval, pendingIntent);
//        Toast.makeText(this, "Alarm Set every " + ((interval/1000) / 60) + " minutes", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarmIfExists(Context mContext,int requestCode){
        try{
            Intent myIntent = new Intent(mContext, QuestionReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, requestCode, myIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am=(AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
            am.cancel(pendingIntent);
            pendingIntent.cancel();
            Log.d("DEBUG remove alarm", "true");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
