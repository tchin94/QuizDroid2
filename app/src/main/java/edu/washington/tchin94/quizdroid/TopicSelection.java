package edu.washington.tchin94.quizdroid;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class TopicSelection extends ActionBarActivity {

    private TopicRepo topicRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_selection);

        ListView topics = (ListView) findViewById(R.id.topics);
        QuizApp quizApp = (QuizApp)getApplication();
        topicRepo = quizApp.getTopicRepo();
        if (topicRepo.getTopicList().size() == 0) {
            addTopics();
        }

        MyCustomAdapter adapter = new MyCustomAdapter();
        topics.setAdapter(adapter);
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

    private void addTopics() {
        String[] topics = getTopicNames();
        String[] shortDesc = getTopicShortDesc();
        String[] longDesc = getTopicLongDesc();
        for (int i = 0; i < topics.length; i++) {
            String topicName = topics[i];
            Topic newTopic = new Topic();
            newTopic.setTopicName(topicName);
            newTopic.setShortDesc(shortDesc[i]);
            newTopic.setLongDesc(longDesc[i]);
            ArrayList<Quiz> questions = getQuestions(topicName);
            newTopic.setQuestions(questions);
            topicRepo.addTopic(newTopic);
        }
    }

    private String[] getTopicNames() {
        String[] topicNames = new String[] {"Math", "Physics", "Marvel Super Heroes"};
        return topicNames;
    }

    private String[] getTopicShortDesc() {
        String[] shortDesc = new String[] {
                "Math, Numbers, Equations, Etc.",
                "Physics, the law of nature",
                "Heroes from the Marvel Universe",
        };
        return shortDesc;
    }

    private String[] getTopicLongDesc() {
        String[] longDesc = new String[] {
                "Mathematics is the study of topics such as quantity (numbers), structure, space, and change. - Wiki",
                "Physics is the natural science that involves the study of matter and its motion through space and time, along with related concepts such as energy and force. -Wiki",
                "Cool heroes fighting bad guys."
        };
        return longDesc;
    }

    private ArrayList<Quiz> getQuestions(String topicName) {
        ArrayList<Quiz> questions = new ArrayList<Quiz>();
        String[] questionList = getQuestionList(topicName);
        String[] answers = getAnswers(topicName);
        int[] correctAnswers = getCorrectAnswers(topicName);

        for (int i = 0; i < questionList.length; i++) {
            Quiz question = new Quiz();
            question.setQuestion(questionList[i]);
            question.setCorrectAnswer(correctAnswers[i]);
            question.setAnswer1(answers[0 + i * 4]);
            question.setAnswer2(answers[1 + i * 4]);
            question.setAnswer3(answers[2 + i * 4]);
            question.setAnswer4(answers[3 + i * 4]);
            questions.add(question);
        }

        return questions;
    }

    private int[] getCorrectAnswers(String topicName) {
        if (topicName.equals("Math")) {
            int[] correctAnswers = new int[] {2, 0, 3, 1, 0};
            return correctAnswers;
        } else if (topicName.equals("Physics")) {
            int[] correctAnswers = new int[] {0, 3, 0, 2};
            return correctAnswers;
        } else {
            int[] correctAnswers = new int[] {1, 0, 0, 2};
            return correctAnswers;
        }
    }

    private String[] getAnswers(String topicName) {
        if (topicName.equals("Math")) {
            String[] answers = new String[] {
                    "0", "1", "2", "3",
                    "0", "1", "2", "3",
                    "0", "1", "2", "3",
                    "0", "-1", "2", "3",
                    "0", "1", "2", "3",
            };
            return answers;
        } else if (topicName.equals("Physics")) {
            String[] answers = new String[] {
                    "Newtons", "Kilograms", "Meters", "Seconds",
                    "Miles", "Bananas", "Apples", "Meters",
                    "9.8m/s^2", "100lbs", "42", "0",
                    "Money", "Orange", "Apple", "Dragons"
            };
            return answers;
        } else {
            String[] answers = new String[] {
                    "Spider Man", "Spider-Man", "Man Spider", "Man-Spider",
                    "Batman", "Deadpool", "Mr. Fantastic", "Iron Man",
                    "Ted Neward", "Deadpool", "Mr. Fantastic", "Iron Man",
                    "Kingpin", "Magneto", "Thanos", "Doctor Doom"
            };
            return answers;
        }
    }

    private String[] getQuestionList(String topicName) {
        if (topicName.equals("Math")) {
            String[] questionList = new String[]{
                    "1 + 1 =?",
                    "1 - 1 =?",
                    "1 + 2 =?",
                    "1 - 2 =?",
                    "2 - 2 =?"
            };
            return questionList;
        } else if (topicName.equals("Physics")) {
            String[] questionList = new String[]{
                    "What's the unit of force?",
                    "What's the standard unit for measuring length?",
                    "What is gravity on Earth?",
                    "What fell on Newton?"
            };
            return questionList;
        } else {
            String[] questionList = new String[]{
                    "Who does whatever a spider can?",
                    "Who isn't a Marvel Super Hero?",
                    "Who is a genius, billionaire, playboy, philanthropist?",
                    "Who is the archenemy of the Avengers?"
            };
            return questionList;
        }
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
                ImageView image = (ImageView) convertView.findViewById(R.id.list_image_icon);
                TextView title = (TextView) convertView.findViewById(R.id.list_topic_name);
                TextView description = (TextView) convertView.findViewById(R.id.list_description);
                Topic topic = (Topic) getItem(position);
                title.setText(topic.getTopicName());
                description.setText(topic.getShortDesc());
                image.setImageResource(android.R.drawable.ic_search_category_default);
            }
            return convertView;
        }
    }
}
