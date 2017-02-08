package smartlift.ibesk.ru.smartliftclient;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import smartlift.ibesk.ru.smartliftclient.fragments.LogoFragment;
import smartlift.ibesk.ru.smartliftclient.fragments.QuestionFragment;
import smartlift.ibesk.ru.smartliftclient.model.api.Api;
import smartlift.ibesk.ru.smartliftclient.services.ApiService;
import smartlift.ibesk.ru.smartliftclient.utils.LiftTimer;
import smartlift.ibesk.ru.smartliftclient.views.TimerTextView;

import static smartlift.ibesk.ru.smartliftclient.model.api.Api.ACTION.API_ACTION;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.CHECK;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.EXPAND_ANSWER;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.LOGO;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.PICK_VARIANT;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.QUESTION;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.STATUS;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.TIMER_RESET;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.TIMER_START;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.TIMER_STOP;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        QuestionFragment.AnswerListener, LiftTimer.TimeFinishListener {


    private TimerTextView mTimerTv;
    private LinearLayout mTimerPanel;
    private int QUESTION_TIME = 10 * 1000;
    private int DEADLINE = 6 * 1000;
    private LiftTimer mLiftTimer;
    private QuestionFragment mQFragment;
    private FrameLayout container;
    private BroadcastReceiver mReceiver;
    private TextView mOnlineTv;
    private SharedPreferences mPrefs;
    private ProgressBar mBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String url = mPrefs.getString(ApiService.WS_URL, "");
        if (url.isEmpty()){
            startActivity(new Intent(this, StartActivity.class));
            return;
        }



        // GUI
        mOnlineTv = (TextView) findViewById(R.id.onlineTv);
        mTimerPanel = (LinearLayout) findViewById(R.id.timer_panel);
        mTimerTv = (TimerTextView) findViewById(R.id.timerTv);
        container = (FrameLayout) findViewById(R.id.fragment_container);
        mReceiver = new ApiReceiver();
        mBar = (ProgressBar) findViewById(R.id.progressBar);


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
        DEADLINE = (Integer.parseInt((etTimeRed).getText().toString()) + 1) * 1000;
        QUESTION_TIME = (Integer.parseInt((etTime).getText().toString())) * 1000;
        mBar.setMax(QUESTION_TIME);
        mBar.setProgress(QUESTION_TIME);

        mTimerTv.setTimerText(QUESTION_TIME);
        mTimerTv.setDeadline(DEADLINE);
        mLiftTimer = new LiftTimer(QUESTION_TIME, mTimerTv, this, mBar);

        showLogo();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void testInitTimer() {
        DEADLINE = (Integer.parseInt(
                ((TextView) findViewById(R.id.timerRedEt)).getText().toString()
        ) + 1) * 1000;
        QUESTION_TIME = (Integer.parseInt(
                ((TextView) findViewById(R.id.timerTimeEt)).getText().toString()
        )) * 1000;

        mTimerTv.setTimerText(QUESTION_TIME);
        mTimerTv.setDeadline(DEADLINE);
        mLiftTimer = new LiftTimer(QUESTION_TIME, mTimerTv, this, mBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start socket service
        ApiService.start(this);
        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(this)
                    .registerReceiver(mReceiver, new IntentFilter(API_ACTION));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApiService.goOffline();
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
    public void onAnswer(int clickedVariantId) {
        Log.d("qq", "onAnswer: ");
        if (mLiftTimer != null) {
            mLiftTimer.pause();
        }
        ApiService.sendTelemetry("telemetry-" + clickedVariantId);
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
                case TIMER_START:
                    if (mLiftTimer != null) {
                        mLiftTimer.start();
                    }
                    break;
                case TIMER_STOP:
                    if (mLiftTimer != null) {
                        mLiftTimer.pause();
                    }
                    break;
                case TIMER_RESET:
                    if (mLiftTimer != null) {
                        mLiftTimer.reset();
                    }
                    break;
                case STATUS:
                    if (mOnlineTv != null) {
                        String status = intent.getStringExtra(ApiService.EXTRA_CONTENT);
                        mOnlineTv.setText(status);
                        mOnlineTv.setTextColor(Color.GREEN);
                        if (Api.SOCKET.DOWN.equals(status)){
                            showLogo();
                            mOnlineTv.setTextColor(Color.DKGRAY);
                            rescheduleConnection();
                        }
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
