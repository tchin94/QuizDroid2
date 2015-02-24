package edu.washington.tchin94.quizdroid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;


public class TopicOverview extends ActionBarActivity {

    private String[] questions;
    private String[] answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_overview);

        Intent launcher = getIntent();
        String topicName = launcher.getStringExtra("topicName");
        String topicDescription = launcher.getStringExtra("topicDescription");
        questions = launcher.getStringArrayExtra("questions");
        answers = launcher.getStringArrayExtra("answers");
        int numQuestions = questions.length;

        TextView topicNameText = (TextView) findViewById(R.id.topic_name);
        topicNameText.setText(topicName);
        TextView topicDescriptionText = (TextView) findViewById(R.id.topic_description);
        topicDescriptionText.setText(topicDescription);
        TextView questionCount = (TextView) findViewById(R.id.topic_question_count);
        questionCount.setText("Number of Questions: " + numQuestions);

        Button beginBtn = (Button) findViewById(R.id.begin_btn);
        beginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent firstQuestion = new Intent(TopicOverview.this, Question.class);
                firstQuestion.putExtra("questions", questions);
                firstQuestion.putExtra("answers", answers);

                if (firstQuestion.resolveActivity(getPackageManager()) != null) {
                    startActivity(firstQuestion);
                }

                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_topic_overview, menu);
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
