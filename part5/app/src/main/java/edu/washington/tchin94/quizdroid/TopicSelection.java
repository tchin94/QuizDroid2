package edu.washington.tchin94.quizdroid;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class TopicSelection extends ActionBarActivity {

    private static final int TIME_UNIT = 1000 * 60; //minutes
    private static final int DEFAULT_TIME_INTERVAL = 5;
    private static final String FILENAME = "quizdata.json";

    private TopicRepo topicRepo;
    private PendingIntent pendingIntent;
    private boolean questionIsLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_selection);

        Log.d("Topic Selection Created", "DEBUG");
        checkJsonFile();
        if(!questionIsLoaded)
            clickToReload();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelAlarmIfExists(TopicSelection.this, 0);
    }

    public void checkJsonFile() {
        if(isConnected(this)) {
            setQuestionDownload();
        }
        getData();
    }

    public void clickToReload() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.reload_message)
                .setTitle(R.string.reload_title);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getData();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        getData();
    }

    public void getData() {
        ListView topics = (ListView) findViewById(R.id.topics);
        QuizApp quizApp = (QuizApp)getApplication();
        topicRepo = quizApp.getTopicRepo();

        //only add topics again if the topic was empty the first time
        if (topicRepo.getTopicList().size() == 0) {
            addTopics();
        }

        //sets the listview
        MyCustomAdapter adapter = new MyCustomAdapter();
        topics.setAdapter(adapter);
        if (!adapter.isEmpty()) {
            questionIsLoaded = true;
        } else {
            questionIsLoaded = false;
        }
        AdapterView.OnItemClickListener topicClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String topicName = parent.getItemAtPosition(position).toString();
                Topic topic = topicRepo.getTopic(position);
                Intent topicOverview = new Intent(TopicSelection.this, MultiUseActivity.class);

                topicOverview.putExtra("topic", topic);
                if (topicOverview.resolveActivity(getPackageManager()) != null) {
                    startActivity(topicOverview);
                }
                Log.d(topicName, "DEBUG");
            }
        };

        topics.setOnItemClickListener(topicClickListener);
    }

    public void checkConnection() {
        if (isAirplaneModeOn(this)) {
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage(R.string.connect_error_message)
                    .setTitle(R.string.connect_error_title);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (!isConnected(this)) {
            Toast.makeText(this, "You have no connection :(", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
            return true;
        }else{
            return false;
        }
    }

    private static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;
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

    //sets periodic question download request
    public void setQuestionDownload() {
        Log.d("set question fetching", "DEBUG");
        Intent questionIntent = new Intent(TopicSelection.this, QuestionReceiver.class);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int interval = Integer.parseInt(pref.getString("time_interval", DEFAULT_TIME_INTERVAL + "")) * TIME_UNIT;

        pendingIntent = PendingIntent.getBroadcast(TopicSelection.this, 0, questionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()), interval, pendingIntent);
//        Toast.makeText(this, "Alarm Set every " + ((interval/1000) / 60) + " minutes", Toast.LENGTH_SHORT).show();
    }


    //adds a new topic with descriptions, icon, and name
    private void addTopics() {
        //gets a list of necessary components of all of the topics
        String[] topics = getTopicAttribute("title");
        String[] desc = getTopicAttribute("desc");

        //adds all of the topics into the topic repo
        for (int i = 0; i < topics.length; i++) {
            String topicName = topics[i];
            Topic newTopic = new Topic();
            newTopic.setTopicName(topicName);
            newTopic.setLongDesc(desc[i]);
            ArrayList<Quiz> questions = getQuestions(topicName);
            newTopic.setQuestions(questions);
            newTopic.setIcon(android.R.drawable.ic_search_category_default);
            topicRepo.addTopic(newTopic);
        }
    }

    //retrieves all of the available topic attributes
    private String[] getTopicAttribute(String attribute) {
        String loadedJson = null;
        FileInputStream fis;
        try {
            fis = openFileInput(FILENAME);
            byte[] dataArray = new byte[fis.available()];
            while (fis.read(dataArray) != -1) {
                loadedJson = new String(dataArray);
            }

            JSONArray arr = new JSONArray(loadedJson);
            String[] topicAttribute = new String[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                topicAttribute[i] = obj.getString(attribute);
            }
            return topicAttribute;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    //retrieves all of the available question attributes
    private String[] getQuestionAttribute(String topic, String attribute) {
        String loadedJson = null;
        FileInputStream fis;
        try {
            fis = openFileInput(FILENAME);
            byte[] dataArray = new byte[fis.available()];
            while (fis.read(dataArray) != -1) {
                loadedJson = new String(dataArray);
            }

            JSONArray arr = new JSONArray(loadedJson);
            int i = 0;
            while (i < arr.length()) {
                JSONObject topicObj = arr.getJSONObject(i);
                String topicObjString = topicObj.getString("title");
                Log.d("DEBUG topic name", topicObjString);
                Log.d("DEBUG topic name given", topic);
                if (topic.equals(topicObjString)) {
                    JSONArray questionArr = topicObj.getJSONArray("questions");
                    int arrLength = questionArr.length();
                    String[] questionAttribute = new String[arrLength];
                    for (int j = 0; j < arrLength; j++) {
                        Log.d("DEBUG question", ""+j);
                        JSONObject questionObj = questionArr.getJSONObject(j);
                        Log.d("DEBUG " + attribute, questionObj.getString(attribute));
                        questionAttribute[j] = questionObj.getString(attribute);
                    }
                    return questionAttribute;
                }
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    //retrieves all of the available question answers
    private String[] getQuestionAnswers(String topic) {
        String loadedJson = null;
        FileInputStream fis;
        try {
            fis = openFileInput(FILENAME);
            byte[] dataArray = new byte[fis.available()];
            while (fis.read(dataArray) != -1) {
                loadedJson = new String(dataArray);
            }

            JSONArray arr = new JSONArray(loadedJson);
            int i = 0;
            while (i < arr.length()) {
                JSONObject topicObj = arr.getJSONObject(i);
                String topicObjString = topicObj.getString("title");
                Log.d("DEBUG topic name", topicObjString);
                Log.d("DEBUG topic name given", topic);
                if (topic.equals(topicObjString)) {
                    Log.d("DEBUG topic found", "answers TRUE");
                    JSONArray questionArr = topicObj.getJSONArray("questions");
                    int questionLength = questionArr.length();
                    int arrLength = 4 * questionLength;
                    String[] questionAnswers = new String[arrLength];
                    Log.d("DEBUG answer length", "before filled " + questionAnswers.length);
                    Log.d("DEBUG question length", "before filled " + questionLength);
                    for (int j = 0; j < arrLength; j++) {
                        JSONObject questionObj = questionArr.getJSONObject(j / 4);
                        JSONArray answerArr = questionObj.getJSONArray("answers");
                        questionAnswers[j] = answerArr.getString(j % 4);
                        Log.d("DEBUG answer", answerArr.getString(j % 4));
                    }
                    return questionAnswers;
                }
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new String[0];
    }

    //retrieves all of the questions based on the topic name
    private ArrayList<Quiz> getQuestions(String topicName) {
        ArrayList<Quiz> questions = new ArrayList<Quiz>();
        String[] questionList = getQuestionAttribute(topicName, "text");
        String[] answers = getQuestionAnswers(topicName);
        String[] correctAnswers = getQuestionAttribute(topicName, "answer");

        Log.d("DEBUG answer length", answers.length + "");

        Log.d("DEBUG quest length", (questionList.length * 4) + "");
        //sets the question, answers, and gets the correct answers
        for (int i = 0; i < questionList.length; i++) {
            Quiz question = new Quiz();
            question.setQuestion(questionList[i]);
            question.setCorrectAnswer(Integer.parseInt(correctAnswers[i]) - 1);
            question.setAnswer1(answers[0 + i * 4]);
            question.setAnswer2(answers[1 + i * 4]);
            question.setAnswer3(answers[2 + i * 4]);
            question.setAnswer4(answers[3 + i * 4]);
            questions.add(question);
        }
        return questions;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_topic_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(TopicSelection.this, SettingsActivity.class);
            startActivity(settings);
        }

        return super.onOptionsItemSelected(item);
    }

    Preference.OnPreferenceChangeListener prefChangeListener = new Preference.OnPreferenceChangeListener() {
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            TopicSelection.this.setQuestionDownload();
            return true;
        }
    };

    //custom adapter for list view
    private class MyCustomAdapter extends BaseAdapter {

        private ArrayList<Topic> topics = topicRepo.getTopicList();
        private LayoutInflater mInflater;

        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return topics.size();
        }

        @Override
        public Object getItem(int position) {
            return topics.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            System.out.println("getView " + position + " " + convertView);
            if (convertView ==null) {
                convertView = mInflater.inflate(R.layout.single_row, parent, false);

                //grabs all of the views
                ImageView image = (ImageView) convertView.findViewById(R.id.list_image_icon);
                TextView title = (TextView) convertView.findViewById(R.id.list_topic_name);
                TextView description = (TextView) convertView.findViewById(R.id.list_description);

                //sets the views
                Topic topic = (Topic) getItem(position);
                title.setText(topic.getTopicName());
                description.setText(topic.getLongDesc());
                image.setImageResource(topic.getIcon());
            }
            return convertView;
        }
    }
}
