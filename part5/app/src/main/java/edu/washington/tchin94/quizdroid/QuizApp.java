package edu.washington.tchin94.quizdroid;

import android.app.Application;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class QuizApp extends Application {

    private static QuizApp instance;
    private TopicRepo topicRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        topicRepo = new TopicRepo();
        Log.d("QuizApp Loaded", "Debug");
    }

    public QuizApp() {
        if (instance == null) {
            instance = this;
        } else {
            throw new RuntimeException("error with singleton");
        }
    }

    public static QuizApp instance() {
        return instance;
    }

    //gets topicrepository
    public TopicRepo getTopicRepo() {
        return topicRepo;
    }
}
