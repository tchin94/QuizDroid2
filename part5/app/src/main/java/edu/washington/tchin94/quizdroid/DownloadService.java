package edu.washington.tchin94.quizdroid;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class DownloadService extends IntentService {
    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String link = pref.getString("url_text", "http://tednewardsandbox.site44.com/questions.json");
        String filename = "quizdata.json";
        String tempFileName = "temp.json";
        boolean downloadSuccess = false;
        HttpClient client = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(link);

        //while there is internet


        //while there is connection
        while (isConnected() && !downloadSuccess) {
            //try to grab the json file
            HttpResponse response = null;
            try {
                response = client.execute(getRequest);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode != 200) {
                    downloadFailed();
                    return;
                }
                //puts json file into a reader
                InputStream jsonStream = response.getEntity().getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(jsonStream));
                StringBuilder builder = new StringBuilder();

                //adds the information into a builder
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                //creates the jsonData string from the builder
                String jsonData = builder.toString();
                FileOutputStream outputStream;

                //tries to build the temp file
                try {
                    outputStream = openFileOutput(tempFileName, Context.MODE_PRIVATE);
                    outputStream.write(jsonData.getBytes());
                    outputStream.close();
                    downloadSuccess = true;
                    //finished downloading
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //if download failed somehow, report it
        if (!downloadSuccess) {
            downloadFailed();
        } else {
            //copies temp file over to the quizdata.json file
            File temp = getFileStreamPath(tempFileName);
            try {
                FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            File quizFile = getFileStreamPath(filename);

            //copy over temp data to quizfile
            try {
                copy(temp, quizFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //prints out the json data for debug purposes
        String loadedJson = null;
        FileInputStream fis;
        try {
            fis = openFileInput(filename);
            byte[] dataArray = new byte[fis.available()];
            while (fis.read(dataArray) != -1) {
                loadedJson = new String(dataArray);
                System.out.println("JSON DATA: " + loadedJson);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(loadedJson);
    }

    //check connectivity
    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    //reports download failure
    public void downloadFailed() {
        System.out.println("download failed");
        Intent downloadFail = new Intent(this, DownloadFail.class);
        downloadFail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(downloadFail);

    }

    //copies file over
    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }


}
