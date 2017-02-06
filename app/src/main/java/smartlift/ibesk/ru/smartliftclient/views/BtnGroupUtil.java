package smartlift.ibesk.ru.smartliftclient.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

import smartlift.ibesk.ru.smartliftclient.R;

/**
 * Created by Mikekeke on 30-Jan-17.
 */

public class BtnGroupUtil {
    private Drawable mDefBg, mPressedBg, mCorrectBg, mWrongBg;
    @SuppressLint("UseSparseArrays")
    private Map<Integer, Button> mButtonsMap = new HashMap<>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, Integer> mVarId2BtnId = new HashMap<>();
    private int mCurrentClickedId = -1;
    private Context mContext;

    public BtnGroupUtil(Context context, View.OnClickListener listener, Map<Integer, Button> buttons) {
        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            Button button = entry.getValue();
            button.setOnClickListener(listener);
            mButtonsMap.put(button.getId(), entry.getValue());
            mVarId2BtnId.put(entry.getKey(), button.getId());
        }
        mDefBg = buttons.get(1).getBackground();
        mContext = context;
        mPressedBg = ContextCompat.getDrawable(mContext, R.drawable.bg_pressed_btn);
        mCorrectBg = ContextCompat.getDrawable(mContext, R.drawable.bg_correct_button);
        mWrongBg = ContextCompat.getDrawable(mContext, R.drawable.bg_wrong_button);
    }

    public void handleColoring(int id) {
        if (mCurrentClickedId != -1) {
            mButtonsMap.get(mCurrentClickedId).setBackground(mDefBg);
        }
        mCurrentClickedId = id;
        mButtonsMap.get(mCurrentClickedId).setBackground(mPressedBg);
    }

    public int getCkickedVariantId(int viewId) {
        for (int key : mVarId2BtnId.keySet()) {
            if (mVarId2BtnId.get(key) == viewId)
                return key;
        }
        return -1;
    }

    public boolean handleCheck(int correctVar) {
        if (mCurrentClickedId == -1) return false;
        if (mCurrentClickedId == mVarId2BtnId.get(correctVar)) {
            mButtonsMap.get(mCurrentClickedId).setBackground(mCorrectBg);
            return true;
        } else {
            mButtonsMap.get(mCurrentClickedId).setBackground(mWrongBg);
            mButtonsMap.get(mVarId2BtnId.get(correctVar)).setBackground(mCorrectBg);
            return false;
        }
    }

    public void pickVariant(int num) {
        int btnId = mVarId2BtnId.get(num);
        Button btn = mButtonsMap.get(btnId);
        btn.performClick();
        handleColoring(btnId);
    }
}
