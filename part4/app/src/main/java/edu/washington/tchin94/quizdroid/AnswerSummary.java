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


public class AnswerSummary extends ActionBarActivity {

    private String[] questions;
    private String[] answers;
    private int curQuestion;
    private int score;
    private String yourAnswer;
    private String rightAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_summary);

        //grabbing data from previous activity
        Intent launcher = getIntent();
        questions = launcher.getStringArrayExtra("questions");
        answers = launcher.getStringArrayExtra("answers");
        yourAnswer = launcher.getStringExtra("yourAnswer");
        rightAnswer = launcher.getStringExtra("rightAnswer");
        curQuestion = launcher.getIntExtra("curQuestion", 0);
        score = launcher.getIntExtra("score", 0);

        //set text in layout
        TextView yourAnswerText = (TextView) findViewById(R.id.your_answer);
        yourAnswerText.setText("Your Answer: " + yourAnswer);
        TextView rightAnswerText = (TextView) findViewById(R.id.correct_answer);
        rightAnswerText.setText("Correct Answer: " + rightAnswer);
        TextView totalScoreText = (TextView) findViewById(R.id.total_score);
        totalScoreText.setText("You have " + score + " out of " + questions.length +" correct.");

        //continue button
        Button nextBtn = (Button) findViewById(R.id.next_btn);
        if (curQuestion == questions.length) {
            nextBtn.setText("Finish");
        }
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (curQuestion == questions.length) {
                    Intent beginning = new Intent(AnswerSummary.this, TopicSelection.class);
                    beginning.addFlags(beginning.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(beginning);
                    finish();
                } else {
                    Intent nextQuestion = new Intent(AnswerSummary.this, Question.class);
                    nextQuestion.putExtra("questions", questions);
                    nextQuestion.putExtra("answers", answers);
                    nextQuestion.putExtra("curQuestion", curQuestion);
                    nextQuestion.putExtra("score", score);
                    startActivity(nextQuestion);
                    finish();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_summary, menu);
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
