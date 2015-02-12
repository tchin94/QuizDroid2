package edu.washington.tchin94.quizdroid;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnswerSummaryFragment extends Fragment {

    private String yourAnswerString;
    private String correctAnswerString;
    private int correct;
    private int totalQuestion;

    public AnswerSummaryFragment() {
        // Required empty public constructor
    }


    public static AnswerSummaryFragment newInstance(String yourAnswer, String correctAnswer,
                                                    int correct, int totalQuestion) {
        AnswerSummaryFragment answerSummaryFragment = new AnswerSummaryFragment();
        Bundle args = new Bundle();
        Log.d("your answer: " + yourAnswer, "DEBUG your answer transferred");
        args.putString("yourAnswer", yourAnswer);
        args.putString("correctAnswer", correctAnswer);
        args.putInt("totalQuestion", totalQuestion);
        args.putInt("correct", correct);
        answerSummaryFragment.setArguments(args);
        return answerSummaryFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get back arguments
        yourAnswerString = getArguments().getString("yourAnswer", "");
        correctAnswerString = getArguments().getString("correctAnswer", "");
        correct = getArguments().getInt("correct", 0);
        totalQuestion = getArguments().getInt("totalQuestion", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_answer_summary, container, false);



        TextView yourAnswer = (TextView) rootView.findViewById(R.id.fragment_your_answer);
        yourAnswer.setText("Your Answer: " + yourAnswerString);
        TextView correctAnswer = (TextView) rootView.findViewById(R.id.fragment_correct_answer);
        correctAnswer.setText("Correct Answer: " + correctAnswerString);
        TextView totalScore = (TextView) rootView.findViewById(R.id.fragment_total_score);
        totalScore.setText("You have " + correct + " out of " + totalQuestion + " correct.");

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final MultiUseActivity multiUseActivity = (MultiUseActivity) getActivity();
        Button submitBtn = (Button) multiUseActivity.findViewById(R.id.advance_btn);
        if (multiUseActivity.getCurQuestion() == multiUseActivity.getNumQuestions()) {
            submitBtn.setText("Finish");
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent beginning = new Intent(multiUseActivity, TopicSelection.class);
                    beginning.addFlags(beginning.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(beginning);
                    multiUseActivity.finish();
                }
            });
        } else {
            submitBtn.setText("Next");
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QuestionFragment questionFragment = new QuestionFragment();
                    FragmentManager fragmentManager = getActivity().getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment, questionFragment);
                    fragmentTransaction.commit();
                }
            });
        }

    }


    }
