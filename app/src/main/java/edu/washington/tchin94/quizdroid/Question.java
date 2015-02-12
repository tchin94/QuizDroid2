package edu.washington.tchin94.quizdroid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class Question extends ActionBarActivity {

    private String[] questions;
    private String[] answers;
    private int curQuestion;
    private int score;
    private String yourAnswer;
    private String rightAnswer;
    private boolean selected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        //grabbing data from previous activity
        Intent launcher = getIntent();
        questions = launcher.getStringArrayExtra("questions");
        answers = launcher.getStringArrayExtra("answers");
        curQuestion = launcher.getIntExtra("curQuestion", 0);
        score = launcher.getIntExtra("score", 0);

        //setting text in layout
        TextView question = (TextView) findViewById(R.id.question);
        question.setText(questions[curQuestion]);

        //sets the radio buttons
        RadioButton answer1 = (RadioButton) findViewById(R.id.answer1);
        RadioButton answer2 = (RadioButton) findViewById(R.id.answer2);
        RadioButton answer3 = (RadioButton) findViewById(R.id.answer3);
        RadioButton answer4 = (RadioButton) findViewById(R.id.answer4);

        ArrayList<RadioButton> answer = new ArrayList<RadioButton>();
        answer.add(answer1); answer.add(answer2); answer.add(answer3); answer.add(answer4);
        setRadioAnswers(answer, answers, curQuestion);

        Button submitBtn = (Button) findViewById(R.id.submit_btn);
        submitBtn.setEnabled(selected);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent answerSummary = new Intent(Question.this, AnswerSummary.class);
                int outcomeScore = score;

                //add a point if correct
                RadioGroup group = (RadioGroup) findViewById(R.id.answer_group);
                RadioButton selected = (RadioButton) findViewById(group.getCheckedRadioButtonId());
                if (selected.getContentDescription().equals("correct")) {
                    outcomeScore++;
                }

                //sends data to the next activity
                answerSummary.putExtra("questions", questions);
                answerSummary.putExtra("answers", answers);
                answerSummary.putExtra("curQuestion", curQuestion + 1);
                answerSummary.putExtra("yourAnswer", selected.getText());
                answerSummary.putExtra("rightAnswer", rightAnswer);
                answerSummary.putExtra("score", outcomeScore);

                if (answerSummary.resolveActivity(getPackageManager()) != null) {
                    startActivity(answerSummary);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("selected", selected);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        selected = savedInstanceState.getBoolean("selected");
    }

    //sets the answers and find which one is right
    public void setRadioAnswers(ArrayList<RadioButton> answer, String[] answers, int curQuestion) {
        for (int i = 0; i < 4; i++) {
            String curAnswer = answers[i + (curQuestion * 4)];
            if (curAnswer.endsWith("***")) {
                curAnswer = curAnswer.substring(0, curAnswer.length() - 3);
                rightAnswer = curAnswer;
                answer.get(i).setContentDescription("correct");
            } else {
                answer.get(i).setContentDescription("wrong");
            }
            answer.get(i).setText(curAnswer);
            answer.get(i).setOnClickListener(radioClickListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_question, menu);
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

    View.OnClickListener radioClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button submitBtn = (Button) findViewById(R.id.submit_btn);
            selected = true;
            submitBtn.setEnabled(selected);
        }
    };
}
