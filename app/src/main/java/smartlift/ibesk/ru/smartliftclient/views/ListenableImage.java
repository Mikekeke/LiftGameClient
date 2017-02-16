package smartlift.ibesk.ru.smartliftclient.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import smartlift.ibesk.ru.smartliftclient.R;

/**
 * Created by ibes on 16.02.17.
 */

public class ListenableImage extends ImageView {
    
    @Nullable
    private TextView mTextToAdjust = null;
    @Nullable
    private DisplayMetrics mDm = null;
    
    public ListenableImage(Context context) {
        super(context);
    }

    public ListenableImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListenableImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mTextToAdjust != null) {
            mTextToAdjust.setPadding((int) (right + getResources().getDimension(R.dimen.question_text_hor_padding)),
                    0, 
                    mTextToAdjust.getPaddingRight(),
                    0);
        }
    }
    
    public  void setTextViewToAdjust(TextView textView, DisplayMetrics dm) {
        mTextToAdjust = textView;
    }
    
    public void removeAdjustableTextView() {
        mTextToAdjust = null;
        mDm = null;
    }
}
