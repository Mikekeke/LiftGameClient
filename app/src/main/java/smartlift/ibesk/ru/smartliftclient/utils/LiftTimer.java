package smartlift.ibesk.ru.smartliftclient.utils;

import android.os.CountDownTimer;

import smartlift.ibesk.ru.smartliftclient.views.TimerTextView;

public class LiftTimer {
    private final long mInitialTime;
    private long mTimeLeft;
    private TimerTextView mTv;
    private CountDownTimer mTimer;

    public interface TimeFinishListener {
        void onTimeFinish();
    }

    private TimeFinishListener mFinishListener;

    public LiftTimer(long timeLeft, TimerTextView tv, TimeFinishListener listener) {
        mInitialTime = timeLeft;
        mTimeLeft = timeLeft;
        mTv = tv;
        mFinishListener = listener;
    }

    public void start() {
        if (mTimer != null) return;
        mTimer = new CountDownTimer(mTimeLeft, 1000) {
            @Override
            public void onTick(long left) {
                mTv.setTimerText(left);
                mTimeLeft = left;
            }

            @Override
            public void onFinish() {
                mTv.finish();
                mTimeLeft = 0;
                if (mFinishListener != null) {
                    mFinishListener.onTimeFinish();
                }
            }
        }.start();
    }

    public void pause() {
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
