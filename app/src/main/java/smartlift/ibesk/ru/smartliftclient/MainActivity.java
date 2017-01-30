package smartlift.ibesk.ru.smartliftclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import smartlift.ibesk.ru.smartliftclient.fragments.QuestionFragment;
import smartlift.ibesk.ru.smartliftclient.utils.JsonHolder;
import smartlift.ibesk.ru.smartliftclient.utils.LiftTimer;
import smartlift.ibesk.ru.smartliftclient.views.TimerTextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        QuestionFragment.AnswerListener, LiftTimer.TimeFinishListener {


    private TimerTextView mTimerTv;
    private final long TEST_TIME = 10 * 1000;
    private long DEADLINE = 6 * 1000;
    private LiftTimer mLiftTimer;
    private QuestionFragment mQFragment;
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTimerTv = (TimerTextView) findViewById(R.id.timerTv);
        mTimerTv.setDeadline(DEADLINE);
        mTimerTv.setTimerText(TEST_TIME);
        mLiftTimer = new LiftTimer(TEST_TIME, mTimerTv, this);
        findViewById(R.id.btnLoadQuestion).setOnClickListener(this);
        findViewById(R.id.btnStartTimer).setOnClickListener(this);
        findViewById(R.id.btnPauseTimer).setOnClickListener(this);
        findViewById(R.id.btnResetTimer).setOnClickListener(this);
        findViewById(R.id.btnCheck).setOnClickListener(this);
        findViewById(R.id.btnAnswer).setOnClickListener(this);
        container = (FrameLayout) findViewById(R.id.fragment_container);
    }

    private void startQuestion() {
        insertQuestionFragment();
        container.setEnabled(true);
        if (mLiftTimer != null) {
            mLiftTimer.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoadQuestion:
                startQuestion();
                break;
            case R.id.btnStartTimer:
                if (mLiftTimer != null) {
                    mLiftTimer.start();
                }
                break;
            case R.id.btnPauseTimer:
                if (mLiftTimer != null) {
                    mLiftTimer.pause();
                }
                break;
            case R.id.btnResetTimer:
                if (mLiftTimer != null) {
                    mLiftTimer.reset();
                }
                break;
            case R.id.btnCheck:
                if (mQFragment != null) {
                    mQFragment.check();
                }
                break;
            case R.id.btnAnswer:
                if (mQFragment != null) {
                    mQFragment.showAnswer();
                }
            default: // empty
        }
    }

    private void insertQuestionFragment() {
        String question = JsonHolder.getQuestion();
        Fragment fragment = QuestionFragment.newInstance(question);
        getSupportFragmentManager().beginTransaction()
                .replace(container.getId(), fragment).commit();
        mQFragment = (QuestionFragment) fragment;
    }

    @Override
    public void onAnswer() {
        if (mLiftTimer != null) {
            mLiftTimer.pause();
        }
    }

    @Override
    public void onTimeFinish() {
        Log.d("qq", "onTimeFinish: ");
        if (mQFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(mQFragment).commit();
        }
    }
}
