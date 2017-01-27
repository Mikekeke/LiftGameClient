package smartlift.ibesk.ru.smartliftclient.views;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import smartlift.ibesk.ru.smartliftclient.R;

/**
 * Created by Mikekeke on 27-Jan-17.
 */

public class TimerTextView extends TextView {
    private DateFormat mFormatter = new SimpleDateFormat("HH:mm:ss");
    private Date mDate = new Date();
    private long mDeadline = -1;
    private int mDefaultColor;
    public TimerTextView(Context context) {
        super(context);
    }

    public TimerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDefaultColor = getCurrentTextColor();
    }

    public TimerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTimerText(long time) {
        checkDeadline(time);
        super.setText(time2text(time));
    }

    private String time2text(long time) {
        mDate.setTime(time);
        String time0 = mFormatter.format(mDate);
        return time0;
    }

    public void finish() {
        setText(R.string.time_is_up);
    }

    public void setDeadline(long deadline) {
        mDeadline = deadline;
    }

    private void checkDeadline(long timeLeft) {
        if (mDeadline != -1 && getCurrentTextColor() != Color.RED &&
                timeLeft < mDeadline) {
            Log.d("qq", "setColor: ");
            setTextColor(Color.RED);
        }
    }

    public void reset(){
        setTextColor(mDefaultColor);
    }
}
