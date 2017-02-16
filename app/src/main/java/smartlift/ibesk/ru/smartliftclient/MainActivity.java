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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import smartlift.ibesk.ru.smartliftclient.fragments.LogoFragment;
import smartlift.ibesk.ru.smartliftclient.fragments.QuestionFragment;
import smartlift.ibesk.ru.smartliftclient.model.api.Api;
import smartlift.ibesk.ru.smartliftclient.services.ApiService;
import smartlift.ibesk.ru.smartliftclient.utils.LiftTimer;

import static smartlift.ibesk.ru.smartliftclient.model.api.Api.ACTION.API_ACTION;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.CHECK;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.EXPAND_ANSWER;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.LOGO;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.PICK_VARIANT;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.QUESTION;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.STATUS;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.TIMER_RESET;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.TIMER_SET;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.TIMER_START;
import static smartlift.ibesk.ru.smartliftclient.model.api.Api.METHOD.TIMER_STOP;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        QuestionFragment.AnswerListener, LiftTimer.TimeFinishListener {

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
    private ImageView mOverlayImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String url = mPrefs.getString(Settings.WS_URL, "");
        if (url.isEmpty()) {
            startActivity(new Intent(this, StartActivity.class));
            return;
        }

        // GUI
        mOnlineTv = (TextView) findViewById(R.id.onlineTv);
        mTimerPanel = (LinearLayout) findViewById(R.id.timer_panel);
        mTimerPanel.setAlpha(0.0f);
        container = (FrameLayout) findViewById(R.id.fragment_container);
        mReceiver = new ApiReceiver();
        mBar = (ProgressBar) findViewById(R.id.progressBar);

        //Timer stuff
        DEADLINE = 11 * 1000;
        QUESTION_TIME = 60 * 1000;
        mBar.setMax(QUESTION_TIME);
        mBar.setProgress(QUESTION_TIME);
        mLiftTimer = new LiftTimer(QUESTION_TIME, this, mBar);

        mOverlayImage = (ImageView) findViewById(R.id.overlay_image);

        showLogo();
        // TODO: 10-Feb-17 testing!
        //        String q = "{\"number\":1,\"name\":\"Вопрос 12\",\"question\":\"Как вы думаете, что нужно сделать космонавту, перед тем как лечь спать в космосе?\",\"correctVar\":2,\"answer\":\"Ответ на впорос 2\",\"status\":\"не задан\",\"img1\":\"\",\"img2\":\"\",\"variants\":{\"1\":\"22\",\"2\":\"вариант 2\",\"3\":\"вариант3\",\"4\":\"вариант 4\"}}";
        //        startQuestion(q);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mTimerPanel.animate()
                    .translationY(mTimerPanel.getHeight());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    private void showLogo() {
        if (mOverlayImage != null) {
            if (mOverlayImage.getVisibility() == View.VISIBLE) {
                mOverlayImage.setVisibility(View.INVISIBLE);
                mOverlayImage.setImageBitmap(null);
            }
        }
        mTimerPanel.animate()
                .translationY(mTimerPanel.getHeight());
        if (mLiftTimer != null) {
            mLiftTimer.pause();
        }
        Fragment fragment = LogoFragment.newInstance();
        if (getSupportFragmentManager().findFragmentByTag("logo") == null) {
            Log.d("qq", "showLogo: ");
            getSupportFragmentManager().beginTransaction()
                    .replace(container.getId(), fragment, "logo").commit();
        }
        ApiService.sendTelemetry("logo-ЛОГОТИП");
    }

    private void startQuestion(String question) {
        mTimerPanel.setAlpha(1.0f);
        mTimerPanel.animate()
                .translationY(0);
        insertQuestionFragment(question);

        // TODO: 10-Feb-17 PUT BACK!
        if (mLiftTimer != null) {
            mLiftTimer.reset();
            mLiftTimer.start();
        }
        ApiService.sendTelemetry("question");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default: // empty
        }
    }

    private void insertQuestionFragment(String question) {
        Fragment fragment = QuestionFragment.newInstance(question);
        getSupportFragmentManager().beginTransaction()
                .disallowAddToBackStack()
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
        //        endGame();
    }

    private class ApiReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String method = intent.getStringExtra(ApiService.EXTRA_METHOD);
            switch (method) {
                case QUESTION:
                    startQuestion(intent.getStringExtra(ApiService.EXTRA_CONTENT));
                    break;
                case CHECK:
                    if (mQFragment != null) {
                        if (mLiftTimer != null) {
                            mLiftTimer.pause();
                        }
                        mQFragment.check();
                    }
                    break;
                case LOGO:
                    showLogo();
                    break;
                case PICK_VARIANT:
                    forcePickVariant(intent.getStringExtra(ApiService.EXTRA_CONTENT));
                    break;
                case EXPAND_ANSWER:
                    showAnswer();

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
                        mBar.setProgress(DEADLINE * 1000);
                    }
                    break;
                case TIMER_SET:
                    String s = intent.getStringExtra(ApiService.EXTRA_CONTENT);
                    Log.d("qq", "onReceive: " + s);
                    break;

                case STATUS:
                    if (mOnlineTv != null) {
                        String status = intent.getStringExtra(ApiService.EXTRA_CONTENT);
                        mOnlineTv.setText(status);
                        mOnlineTv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.online));
                        if (Api.SOCKET.DOWN.equals(status)) {
                            showLogo();
                            mOnlineTv.setTextColor(Color.LTGRAY);
                            rescheduleConnection();
                        }
                    }
                    break;
                default: //empty
            }
        }
    }

    private void showAnswer() {
        if (mLiftTimer != null) {
            mLiftTimer.pause();
        }
        mTimerPanel.animate()
                .translationY(mTimerPanel.getHeight());
        if (mQFragment != null) {
            mQFragment.showAnswer();
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
        if (mQFragment == null) {
            return;
        }
        try {
            int num = Integer.parseInt(variantNum);
            mQFragment.forcePickVariant(num);
        } catch (NumberFormatException e) {
            Log.e("qq", "forcePickVariant: ", e);
        }
    }

    private void endGame() {
        Fragment fragment = LogoFragment.endGameInstance();
        getSupportFragmentManager().beginTransaction().disallowAddToBackStack()
                .replace(container.getId(), fragment).commitAllowingStateLoss();
        ApiService.sendTelemetry("logo-ЛОГОТИП - ВРЕМЯ ВЫШЛО!");
    }
}
