package smartlift.ibesk.ru.smartliftclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import android.widget.LinearLayout;
import smartlift.ibesk.ru.smartliftclient.fragments.LogoFragment;
import smartlift.ibesk.ru.smartliftclient.fragments.QuestionFragment;
import smartlift.ibesk.ru.smartliftclient.services.ApiService;
import smartlift.ibesk.ru.smartliftclient.utils.JsonHolder;
import smartlift.ibesk.ru.smartliftclient.utils.LiftTimer;
import smartlift.ibesk.ru.smartliftclient.views.TimerTextView;

import static smartlift.ibesk.ru.smartliftclient.model.api.Api.ACTION.*;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        QuestionFragment.AnswerListener, LiftTimer.TimeFinishListener {


    private TimerTextView mTimerTv;
    private LinearLayout mTimerPanel;
    private final long TEST_TIME = 10 * 1000;
    private long DEADLINE = 6 * 1000;
    private LiftTimer mLiftTimer;
    private QuestionFragment mQFragment;
    private FrameLayout container;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTimerPanel = (LinearLayout) findViewById(R.id.timer_panel);
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
        findViewById(R.id.btnServer).setOnClickListener(this);
        container = (FrameLayout) findViewById(R.id.fragment_container);
        mReceiver = new ApiReceiver();
        showLogo();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(mReceiver, new IntentFilter(API_ACTION));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        }
    }

    private void startQuestion(String question) {
        if (mTimerPanel.getVisibility() == View.GONE) mTimerPanel.setVisibility(View.VISIBLE);
        insertQuestionFragment(question);
        if (mLiftTimer != null) {
            mLiftTimer.start();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoadQuestion:
                startQuestion(JsonHolder.getQuestion());
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

            case R.id.btnServer:
                ApiService.start(this);

            default: // empty
        }
    }

    private void insertQuestionFragment(String question) {
        Fragment fragment = QuestionFragment.newInstance(question);
        getSupportFragmentManager().beginTransaction().disallowAddToBackStack()
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
        endGame();
    }

    private class ApiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) return;
            String method = intent.getStringExtra(ApiService.EXTRA_METHOD);
            switch (method) {
                case QUESTION:
                    startQuestion(intent.getStringExtra(ApiService.EXTRA_CONTENT));
                    break;
                case CHECK:
                    if (mQFragment != null) mQFragment.check();
                    break;
                case LOGO:
                    showLogo();
                    break;
                case PICK_VARIANT:
                    forcePickVariant(intent.getStringExtra(ApiService.EXTRA_CONTENT));
                    break;
                case EXPAND_ANSWER:
                    if (mQFragment != null) {
                        mQFragment.showAnswer();
                    }
                    break;

                default: //empty
            }
        }
    }

    private void forcePickVariant(String variantNum) {
        if (mQFragment == null) return;
        try {
            int num = Integer.parseInt(variantNum);
            mQFragment.forcePickVariant(num);
        } catch (NumberFormatException e) {
            Log.e("qq", "forcePickVariant: ", e);
        }
    }

    private void showLogo() {
        if (mLiftTimer != null) {
            mLiftTimer.pause();
        }
        Fragment fragment = LogoFragment.newInstance();
        if (getSupportFragmentManager().findFragmentByTag("logo") == null) {
            Log.d("qq", "showLogo: ");
            getSupportFragmentManager().beginTransaction().disallowAddToBackStack()
                    .replace(container.getId(), fragment, "logo").commit();
        }
    }

    private void endGame() {
        Fragment fragment = LogoFragment.endGameInstance();
        getSupportFragmentManager().beginTransaction().disallowAddToBackStack()
                .replace(container.getId(), fragment).commit();
    }
}
