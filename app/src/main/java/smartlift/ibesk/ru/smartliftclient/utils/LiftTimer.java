package smartlift.ibesk.ru.smartliftclient.utils;

import android.os.CountDownTimer;
import android.widget.ProgressBar;

import smartlift.ibesk.ru.smartliftclient.services.ApiService;

public class LiftTimer {
    private final long mInitialTime;
    private long mTimeLeft;
    private CountDownTimer mTimer;
    private ProgressBar mBar;

    public interface TimeFinishListener {
        void onTimeFinish();
    }

    private TimeFinishListener mFinishListener;

    public LiftTimer(int timeLeft,TimeFinishListener listener, ProgressBar bar) {
        mInitialTime = timeLeft;
        mTimeLeft = timeLeft;
        mFinishListener = listener;
        mBar = bar;

    }

    public void start() {
//        ApiService.sendTelemetry("timer-Идёт");
        if (mTimer != null) return;
        mTimer = new CountDownTimer(mTimeLeft, 1000) {
            @Override
            public void onTick(long left) {
                ApiService.sendTelemetry("timer-" + left);
                mTimeLeft = left;
                mBar.setProgress((int) left);
            }

            @Override
            public void onFinish() {
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
        mBar.setProgress((int) mTimeLeft);
    }
}
