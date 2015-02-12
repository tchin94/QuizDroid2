package edu.washington.tchin94.quizdroid;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TopicOverviewFragment extends Fragment {

    private String topicName;
    private String topicDescription;
    private int questionCount;

    public TopicOverviewFragment() {
        // Required empty public constructor
    }

    public static TopicOverviewFragment newInstance(String topicName, String topicDesc, int count) {
        TopicOverviewFragment topicOverviewFragment = new TopicOverviewFragment();
        Bundle args = new Bundle();
        args.putInt("QuestionCount", count);
        args.putString("TopicName", topicName);
        args.putString("TopicDescription", topicDesc);
        topicOverviewFragment.setArguments(args);
        return topicOverviewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get back arguments
        topicName = getArguments().getString("TopicName", "");
        topicDescription = getArguments().getString("TopicDescription", "");
        questionCount = getArguments().getInt("QuestionCount", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_topic_overview, container, false);
        TextView topic = (TextView) rootView.findViewById(R.id.frag_topic_text);
        topic.setText(topicName);
        TextView topicDesc = (TextView) rootView.findViewById(R.id.frag_topic_desc_text);
        topicDesc.setText(topicDescription);
        TextView topicQuestCount = (TextView) rootView.findViewById(R.id.frag_topic_question_count);
        topicQuestCount.setText("Number of Questions: " + questionCount);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button beginBtn = (Button) getActivity().findViewById(R.id.advance_btn);
        beginBtn.setText("Begin");
        beginBtn.setOnClickListener(new View.OnClickListener() {
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
