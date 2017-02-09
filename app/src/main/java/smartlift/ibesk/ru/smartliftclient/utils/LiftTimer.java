package smartlift.ibesk.ru.smartliftclient.utils;

import android.os.CountDownTimer;
import android.widget.ProgressBar;

import smartlift.ibesk.ru.smartliftclient.services.ApiService;
import smartlift.ibesk.ru.smartliftclient.views.TimerTextView;

public class LiftTimer {
    private final long mInitialTime;
    private long mTimeLeft;
    private TimerTextView mTv;
    private CountDownTimer mTimer;
    private ProgressBar mBar;

    public interface TimeFinishListener {
        void onTimeFinish();
    }

    private TimeFinishListener mFinishListener;

    public LiftTimer(int timeLeft, TimerTextView tv, TimeFinishListener listener, ProgressBar bar) {
        mInitialTime = timeLeft;
        mTimeLeft = timeLeft;
        mTv = tv;
        mFinishListener = listener;
        mBar = bar;

    }

    public void start() {
//        ApiService.sendTelemetry("timer-Идёт");
        if (mTimer != null) return;
        mTimer = new CountDownTimer(mTimeLeft, 1000) {
            @Override
            public void onTick(long left) {
                mTv.setTimerText(left);
                ApiService.sendTelemetry("timer-" + mTv.getText());
                mTimeLeft = left;
                mBar.setProgress((int) left);
            }

            @Override
            public void onFinish() {
                mTv.finish();
                mTimeLeft = 0;
                if (mFinishListener != null) {
                    mFinishListener.onTimeFinish();
                }
                mBar.setProgress(0);
            }
        }.start();
    }

    public void pause() {
//        ApiService.sendTelemetry("timer-" + mTimeLeft);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void reset() {
        pause();
        mTimeLeft = mInitialTime;
        mTv.setTimerText(mTimeLeft);
        mTv.reset();
    }
}
