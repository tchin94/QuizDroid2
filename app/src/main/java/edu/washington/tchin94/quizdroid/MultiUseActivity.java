package edu.washington.tchin94.quizdroid;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MultiUseActivity extends ActionBarActivity {

    private Topic topic;
    private ArrayList<Quiz> questions;
    private String topicDescription;
    private int numQuestions;
    private int correct;
    private int curQuestion;

    public Quiz getQuestion() {
        return questions.get(curQuestion);
    }

    public int getNumQuestions() {
        return numQuestions;
    }

    public int getCorrect() {
        return correct;
    }

    public int getCurQuestion() {
        return curQuestion;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_use);

        //grabs the questions and answers from previous intent
        Intent launcher = getIntent();
        topic = (Topic) launcher.getSerializableExtra("topic");
        questions = topic.getQuestions();
        topicDescription = topic.getLongDesc();
        numQuestions = questions.size();

        if (savedInstanceState == null) {
            //sets topic overview for first time
            curQuestion = 0;
            correct = 0;
            TopicOverviewFragment topicOverviewFrag =
                    TopicOverviewFragment.newInstance(topic.getTopicName(), topicDescription, numQuestions);
            FragmentManager fragManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
            fragmentTransaction.add(R.id.fragment, topicOverviewFrag);
            fragmentTransaction.commit();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multi_use, menu);
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("curQuestion", curQuestion);
        savedInstanceState.putInt("correct", correct);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        correct = savedInstanceState.getInt("correct", 0);
        curQuestion = savedInstanceState.getInt("curQuestion", 0);
    }

    public void addPoint() {
        correct++;
    }

    public void incrementCurQuestion() {
        curQuestion++;
    }
}


