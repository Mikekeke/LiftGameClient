package smartlift.ibesk.ru.smartliftclient;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import smartlift.ibesk.ru.smartliftclient.fragments.QuestionFragment;
import smartlift.ibesk.ru.smartliftclient.utils.JsonHolder;
import smartlift.ibesk.ru.smartliftclient.utils.LiftTimer;
import smartlift.ibesk.ru.smartliftclient.views.TimerTextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        QuestionFragment.AnswerListener {

    private TimerTextView mTimerTv;
    private final long TEST_TIME = 10 * 1000;
    private long DEADLINE = 6 * 1000;
    private LiftTimer mLiftTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTimerTv = (TimerTextView) findViewById(R.id.timerTv);
        mTimerTv.setDeadline(DEADLINE);
        mTimerTv.setTimerText(TEST_TIME);
        mLiftTimer = new LiftTimer(TEST_TIME, mTimerTv);
        findViewById(R.id.btnLoadQuestion).setOnClickListener(this);
        findViewById(R.id.btnStartTimer).setOnClickListener(this);
        findViewById(R.id.btnPauseTimer).setOnClickListener(this);
        findViewById(R.id.btnResetTimer).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoadQuestion:
                insertQuestionFragment();
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
            default: // empty
        }
    }

    private void insertQuestionFragment() {
        Fragment fragment = QuestionFragment.newInstance(JsonHolder.JSON);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }

    @Override
    public void onAnswer() {
        if (mLiftTimer != null) {
            mLiftTimer.pause();
        }
    }
}
