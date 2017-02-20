package smartlift.ibesk.ru.smartliftclient.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import smartlift.ibesk.ru.smartliftclient.R;
import smartlift.ibesk.ru.smartliftclient.Settings;
import smartlift.ibesk.ru.smartliftclient.model.Question;
import smartlift.ibesk.ru.smartliftclient.services.ApiService;
import smartlift.ibesk.ru.smartliftclient.views.BtnGroupUtil;
import smartlift.ibesk.ru.smartliftclient.views.ListenableImage;

public class QuestionFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "qq";
    private static final String ARG_QUESTION = "param1";
    private static Gson gson = new Gson();

    private Question mQuestion;
    private int mCorrectVar;

    @Nullable
    private AnswerListener mListener;
    private BtnGroupUtil mBtnGroup;
    private ListenableImage mImageOverlay;
    private View mQuestionBody;
    private TextView qTextTv;
    private SharedPreferences mPrefs;
    private RequestQueue mQueue;

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
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        mQueue = Volley.newRequestQueue(getActivity());
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
        final View v = inflater.inflate(R.layout.fragment_question, container, false);
        if (mQuestion != null) {
            qTextTv = (TextView) v.findViewById(R.id.question_text);
            Typeface robotoTypeface =
                    Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Condensed.ttf");
            qTextTv.setTypeface(robotoTypeface);
            qTextTv.setText(mQuestion.getQuestion());
            qTextTv.setAlpha(0.0f);
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

            mQuestionBody = v.findViewById(R.id.question_body);
        }
        return v;
    }

    private void initImageParams(final View view) {
        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        final int dpi = (int) dm.density;
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        mImageOverlay.setTranslationY(-Settings.App.IMG_Y_SHIFT * dpi);
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mImageOverlay != null && mQuestionBody != null) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        int side = (mQuestionBody.getHeight() + Settings.App.IMG_PLUS_HEIGHT) * dpi;
                        mImageOverlay.setMaxHeight(side);
                        mImageOverlay.setMaxHeight(side);
                        Settings.ImageState.initialize();
                    }
                }
            });
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageOverlay = (ListenableImage) getActivity().findViewById(R.id.overlay_image);

        if (!Settings.ImageState.isInitialized()) {
            initImageParams(view);
        }

        if (TextUtils.isEmpty(mQuestion.getImg1())) {
            qTextTv.animate().alpha(1.0f).setDuration(Settings.App.FADE_INT_TIME);
            return;
        }

        // Loading image and adjusting padding of TextView with question text
        mImageOverlay.setAlpha(0.0f);
        mImageOverlay.setVisibility(View.VISIBLE);
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mImageOverlay != null && mQuestionBody != null) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        String imgUrl = mPrefs.getString(Settings.IMG_URL, "") + "?name=" + mQuestion.getImg1();
                        ImageRequest ir = new ImageRequest(imgUrl, new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                mImageOverlay.setTextViewToAdjust(qTextTv);
                                mImageOverlay.setImageBitmap(response);
                                mImageOverlay.animate().alpha(1.0f).setDuration(Settings.App.FADE_INT_TIME);
                                qTextTv.animate().alpha(1.0f).setDuration(Settings.App.FADE_INT_TIME);
                            }
                        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        qTextTv.animate().alpha(1.0f).setDuration(Settings.App.FADE_INT_TIME);
                                        Log.e(TAG, "onErrorResponse: ", error);
                                    }
                                });
                        mQueue.add(ir);
                    }
                }
            });
        }
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
            if (!TextUtils.isEmpty(mQuestion.getImg1()) && mImageOverlay != null) {
                mImageOverlay.setVisibility(View.INVISIBLE);
                mImageOverlay.removeAdjustableTextView();
                mImageOverlay.setImageBitmap(null);
            }
            Fragment answerFragment = AnswerFragment
                    .newInstance(mQuestion != null ? mQuestion.getAnswer() : "", mQuestion.getImg2());
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
