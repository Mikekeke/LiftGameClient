package smartlift.ibesk.ru.smartliftclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import smartlift.ibesk.ru.smartliftclient.fragments.LogoFragment;
import smartlift.ibesk.ru.smartliftclient.fragments.QuestionFragment;
import smartlift.ibesk.ru.smartliftclient.model.api.Api;
import smartlift.ibesk.ru.smartliftclient.services.ApiService;
import smartlift.ibesk.ru.smartliftclient.utils.JsonHolder;
import smartlift.ibesk.ru.smartliftclient.utils.LiftTimer;
import smartlift.ibesk.ru.smartliftclient.views.TimerTextView;

import static smartlift.ibesk.ru.smartliftclient.model.api.Api.ACTION.API_ACTION;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        QuestionFragment.AnswerListener, LiftTimer.TimeFinishListener {


    private TimerTextView mTimerTv;
    private LinearLayout mTimerPanel;
    private long TEST_TIME = 10 * 1000;
    private long DEADLINE = 6 * 1000;
    private LiftTimer mLiftTimer;
    private QuestionFragment mQFragment;
    private FrameLayout container;
    private BroadcastReceiver mReceiver;
    private TextView mOnlineTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start socket service
        ApiService.start(this);

        // GUI
        mOnlineTv = (TextView) findViewById(R.id.onlineTv);
        mTimerPanel = (LinearLayout) findViewById(R.id.timer_panel);
        mTimerTv = (TimerTextView) findViewById(R.id.timerTv);
        findViewById(R.id.btnLoadQuestion).setOnClickListener(this);
        findViewById(R.id.btnStartTimer).setOnClickListener(this);
        findViewById(R.id.btnPauseTimer).setOnClickListener(this);
        findViewById(R.id.btnResetTimer).setOnClickListener(this);
        findViewById(R.id.btnCheck).setOnClickListener(this);
        findViewById(R.id.btnAnswer).setOnClickListener(this);
        findViewById(R.id.btnServer).setOnClickListener(this);
        findViewById(R.id.btnLogo).setOnClickListener(this);
        container = (FrameLayout) findViewById(R.id.fragment_container);
        mReceiver = new ApiReceiver();


        EditText etTimeRed = (EditText) findViewById(R.id.timerRedEt);
        etTimeRed.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                testInitTimer();
                return false;
            }
        });
        EditText etTime = (EditText) findViewById(R.id.timerTimeEt);
        etTime.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                testInitTimer();
                return false;
            }
        });

        //Timer stuff
        DEADLINE = (Long.parseLong((etTimeRed).getText().toString()) + 1) * 1000;
        TEST_TIME = (Long.parseLong((etTime).getText().toString())) * 1000;

        mTimerTv.setTimerText(TEST_TIME);
        mTimerTv.setDeadline(DEADLINE);
        mLiftTimer = new LiftTimer(TEST_TIME, mTimerTv, this);

        showLogo();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void testInitTimer() {
        DEADLINE = (Long.parseLong(
                ((TextView) findViewById(R.id.timerRedEt)).getText().toString()
        ) + 1) * 1000;
        TEST_TIME = (Long.parseLong(
                ((TextView) findViewById(R.id.timerTimeEt)).getText().toString()
        )) * 1000;

        mTimerTv.setTimerText(TEST_TIME);
        mTimerTv.setDeadline(DEADLINE);
        mLiftTimer = new LiftTimer(TEST_TIME, mTimerTv, this);
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
                break;
            case R.id.btnLogo:
                showLogo();
                break;

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
                case STATUS:
                    if (mOnlineTv != null) {
                        String status = intent.getStringExtra(ApiService.EXTRA_CONTENT);
                        mOnlineTv.setText(status);
                        if (Api.SOCKET.DOWN.equals(status))
                            rescheduleConnection();
                    }
                    break;
                default: //empty
            }
        }
    }

    private void rescheduleConnection() {
        Log.d("qq", "rescheduleConnection: ");
        Intent intent = new Intent(this, ApiService.class);
        PendingIntent pint = PendingIntent.getService(this, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3000, pint);
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
            getSupportFragmentManager().beginTransaction()
                    .replace(container.getId(), fragment, "logo").commit();
        }
    }

    private void endGame() {
        Fragment fragment = LogoFragment.endGameInstance();
        getSupportFragmentManager().beginTransaction().disallowAddToBackStack()
                .replace(container.getId(), fragment).commitAllowingStateLoss();
    }
}
