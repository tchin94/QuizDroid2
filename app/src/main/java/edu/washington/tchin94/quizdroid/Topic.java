package edu.washington.tchin94.quizdroid;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Theo on 2/16/2015.
 */
public class Topic implements Serializable{
    private String topicName;
    private String shortDesc;
    private String longDesc;
    private ArrayList<Quiz> questions;

    public Topic() {

    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public ArrayList<Quiz> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Quiz> questions) {
        this.questions = questions;
    }
}
