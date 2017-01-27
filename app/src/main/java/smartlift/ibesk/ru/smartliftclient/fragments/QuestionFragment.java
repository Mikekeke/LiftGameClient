package smartlift.ibesk.ru.smartliftclient.fragments;

import android.content.Context;
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

import smartlift.ibesk.ru.smartliftclient.R;
import smartlift.ibesk.ru.smartliftclient.model.Question;

public class QuestionFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_QUESTION = "param1";
    private static Gson gson = new Gson();

    private Question mQ;

    private AnswerListener mListener;

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
                mQ = gson.fromJson(reader, Question.class);
            } catch (Exception e) {
                Log.e("qq", "onCreate: ", e);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_question, container, false);
        if (mQ != null) {
            TextView qNameTv = (TextView) v.findViewById(R.id.q_name);
            qNameTv.setText(mQ.getName());
            TextView qTextTv = (TextView) v.findViewById(R.id.q_text);
            qTextTv.setText(mQ.getQuestion());

            Button v1btn = (Button) v.findViewById(R.id.v_1);
            v1btn.setOnClickListener(this);
            v1btn.setText(mQ.getVariants()[0].getText());
            Button v2btn = (Button) v.findViewById(R.id.v_2);
            v2btn.setOnClickListener(this);
            v2btn.setText(mQ.getVariants()[1].getText());
            Button v3btn = (Button) v.findViewById(R.id.v_3);
            v3btn.setOnClickListener(this);
            v3btn.setText(mQ.getVariants()[2].getText());
            Button v4btn = (Button) v.findViewById(R.id.v_4);
            v4btn.setOnClickListener(this);
            v4btn.setText(mQ.getVariants()[3].getText());
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
        if (mListener != null) {
            mListener.onAnswer();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface AnswerListener {
        // TODO: Update argument type and name
        void onAnswer();
    }
}
