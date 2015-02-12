package edu.washington.tchin94.quizdroid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class TopicSelection extends ActionBarActivity {

    private String[] questions;
    private String[] answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_selection);

        String[] values = new String[] {"Math", "Physics", "Marvel Super Heroes"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);
        ListView topics = (ListView) findViewById(R.id.topics);
        topics.setAdapter(adapter);

        AdapterView.OnItemClickListener topicClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String topicName = parent.getItemAtPosition(position).toString();
                String topicDescription = "";
                Intent topicOverview = new Intent(TopicSelection.this, MultiUseActivity.class);
                if (topicName.equals("Math")) {
                    topicDescription = "Mathematics is the study of topics such as quantity (numbers), structure, space, and change. - Wiki";
                    questions = new String[] {
                            "1 + 1 =?",
                            "1 - 1 =?",
                            "1 + 2 =?",
                            "1 - 2 =?"
                    };

                    answers = new String[] {
                            "0", "1", "2***", "3",
                            "0***", "1", "2", "3",
                            "0", "1", "2", "3***",
                            "0", "-1***", "2", "3"
                    };
                } else if (topicName.equals("Physics")) {
                    topicDescription = "Physics is the natural science that involves the study of matter and its motion through space and time, along with related concepts such as energy and force. -Wiki";
                    questions = new String[] {
                            "What's the unit of force?",
                            "What's the standard unit for measuring lenght?",
                            "What is gravity on Earth?",
                            "What fell on Newton?"
                    };

                    answers = new String[] {
                            "Newtons***", "Kilograms", "Meters", "Seconds",
                            "Miles", "Bananas", "Apples", "Meters***",
                            "9.8m/s^2***", "100lbs", "42", "0",
                            "Money", "Orange", "Apple***", "Dragons"
                    };
                } else {
                    topicDescription = "Cool heroes fighting bad guys.";
                    questions = new String[] {
                            "Who does whatever a spider can?",
                            "Who isn't a Marvel Super Hero?",
                            "Who is a genius, billionaire, playboy, philanthropist?",
                            "Who is the archenemy of the Avengers?"
                    };

                    answers = new String[] {
                            "Spider Man", "Spider-Man***", "Man Spider", "Man-Spider",
                            "Batman***", "Deadpool", "Mr. Fantastic", "Iron Man",
                            "Ted Neward***", "Deadpool", "Mr. Fantastic", "Iron Man",
                            "Kingpin", "Magneto", "Thanos***", "Doctor Doom"
                    };
                }

                topicOverview.putExtra("topicName", topicName);
                topicOverview.putExtra("topicDescription", topicDescription);
                topicOverview.putExtra("questions", questions);
                topicOverview.putExtra("answers", answers);

                if (topicOverview.resolveActivity(getPackageManager()) != null) {
                    startActivity(topicOverview);
                }
                Log.d(topicName, "DEBUG");
            }
        };

        topics.setOnItemClickListener(topicClickListener);

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
