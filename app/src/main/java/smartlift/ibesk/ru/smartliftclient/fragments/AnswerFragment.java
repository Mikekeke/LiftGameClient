package smartlift.ibesk.ru.smartliftclient.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import smartlift.ibesk.ru.smartliftclient.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnswerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnswerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ANSWER = "AnswerFragment.ARG_ANSWER";

    private String mAnswer = "";
    public AnswerFragment() {
        // Required empty public constructor
    }

    public static AnswerFragment newInstance(String answer) {
        AnswerFragment fragment = new AnswerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ANSWER, answer);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAnswer = getArguments().getString(ARG_ANSWER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_answer, container, false);
        TextView answerTv = (TextView) v.findViewById(R.id.answer_tv);
        answerTv.setText(mAnswer);
        return v;
    }
}
