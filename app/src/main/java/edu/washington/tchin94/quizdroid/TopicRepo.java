package edu.washington.tchin94.quizdroid;

import java.util.ArrayList;

/**
 * Created by Theo on 2/16/2015.
 */
public class TopicRepo implements TopicRepository {

    ArrayList<Topic> topics;

    public TopicRepo() {
        topics = new ArrayList<Topic>();
    }

    public ArrayList<Topic> getTopicList() {
        return topics;
    }

    public Topic getTopic(int i) throws NullPointerException {
        if (topics == null || topics.get(i) == null) {
            throw new NullPointerException("topic does not exist");
        }
        return topics.get(i);
    }

    public void addTopic(Topic T) {
        topics.add(T);
    }

    public void deleteTopic(Topic T) {
        topics.remove(T);
    }

    public void updateTopic(Topic original, Topic updated) {
        int orig = topics.indexOf(original);
        topics.set(orig, updated);
    }
}
