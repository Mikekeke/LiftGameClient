package smartlift.ibesk.ru.smartliftclient.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import smartlift.ibesk.ru.smartliftclient.R;
import smartlift.ibesk.ru.smartliftclient.model.Question;
import smartlift.ibesk.ru.smartliftclient.services.ApiService;
import smartlift.ibesk.ru.smartliftclient.views.BtnGroupUtil;

public class QuestionFragment extends Fragment implements View.OnClickListener {
    private static final String ARG_QUESTION = "param1";
    private static Gson gson = new Gson();

    private Question mQuestion;
    private int mCorrectVar;

    private AnswerListener mListener;
    private BtnGroupUtil mBtnGroup;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance(String questionString) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION, questionString);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String qString = getArguments().getString(ARG_QUESTION).trim();
            try {
                JsonReader reader = new JsonReader(new StringReader(qString));
                reader.setLenient(true);
                mQuestion = gson.fromJson(reader, Question.class);
            } catch (Exception e) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, LogoFragment.newInstance()).commit();
                Log.e("qq", "onCreate: ", e);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_question, container, false);
        if (mQuestion != null) {
            TextView qTextTv = (TextView) v.findViewById(R.id.question_text);
            Typeface robotoTypeface =
                    Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Condensed.ttf");
            qTextTv.setTypeface(robotoTypeface);
            qTextTv.setText(mQuestion.getQuestion());
            mCorrectVar = mQuestion.getCorrectVar();

            // Setting up buttons
            @SuppressLint("UseSparseArrays")
            Map<Integer, Button> buttonsMap = new HashMap<>();
            Button v1btn = (Button) v.findViewById(R.id.v_1);
            v1btn.setTypeface(robotoTypeface);
            v1btn.setText(mQuestion.getVariants().get(1));
            buttonsMap.put(1, v1btn);
            Button v2btn = (Button) v.findViewById(R.id.v_2);
            v2btn.setText(mQuestion.getVariants().get(2));
            v2btn.setTypeface(robotoTypeface);
            buttonsMap.put(2, v2btn);
            Button v3btn = (Button) v.findViewById(R.id.v_3);
            v3btn.setText(mQuestion.getVariants().get(3));
            v3btn.setTypeface(robotoTypeface);
            buttonsMap.put(3, v3btn);
            Button v4btn = (Button) v.findViewById(R.id.v_4);
            v4btn.setText(mQuestion.getVariants().get(4));
            v4btn.setTypeface(robotoTypeface);
            buttonsMap.put(4, v4btn);
            mBtnGroup = new BtnGroupUtil(getContext(), this, buttonsMap);
        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AnswerListener) {
            mListener = (AnswerListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AnswerListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (mBtnGroup != null) {
            mBtnGroup.handleColoring(v.getId());
        }
        if (mListener != null) {
            mListener.onAnswer(mBtnGroup.getCkickedVariantId(v.getId()));
        }
    }

    public void check() {
        boolean correct = mBtnGroup.handleCheck(mCorrectVar);
        ApiService.sendTelemetry("check-" + (correct ? "Правильно!" : "Ошибка!"));
    }

    public void showAnswer() {
        Activity activity = getActivity();
        if (activity != null) {
            Fragment answerFragment = AnswerFragment
                    .newInstance(mQuestion != null ? mQuestion.getAnswer() : "");
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, answerFragment, "answer").commit();
            ApiService.sendTelemetry(
                    "answer-open");
        }
    }

    public void forcePickVariant(int num) {
        if (mBtnGroup != null) {
            mBtnGroup.pickVariant(num);
        }
    }

    public interface AnswerListener {
        void onAnswer(int variantNumber);
    }
}
