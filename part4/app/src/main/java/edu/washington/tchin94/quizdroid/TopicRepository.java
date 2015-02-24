package edu.washington.tchin94.quizdroid;

import java.util.ArrayList;

/**
 * Created by Theo on 2/16/2015.
 */
public interface TopicRepository {

    public ArrayList<Topic>  getTopicList();

    public Topic getTopic(int i);

    public void addTopic(Topic T);

    public void deleteTopic(Topic T);

    public void updateTopic(Topic original, Topic updated);
}
