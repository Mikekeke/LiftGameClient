package smartlift.ibesk.ru.smartliftclient.views;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mikekeke on 30-Jan-17.
 */

public class BtnGroupUtil {
    private Drawable mDefBg;
    private Drawable mClickedBg;
    private Map<Integer, Button> mButtonsMap = new HashMap<>();
    private Map<Integer, Integer> mVarId2BtnId = new HashMap<>();
    private int mCurrentClickedId = -1;

    public BtnGroupUtil(View.OnClickListener listener, Map<Integer, Button> buttons) {
        for (Map.Entry<Integer, Button> entry : buttons.entrySet()) {
            Button button = entry.getValue();
            button.setOnClickListener(listener);
            mButtonsMap.put(button.getId(), entry.getValue());
            mVarId2BtnId.put(entry.getKey(), button.getId());
        }
        mDefBg = buttons.get(1).getBackground();
    }

    public void handleColoring(int id) {
        if (mCurrentClickedId != -1) {
            mButtonsMap.get(mCurrentClickedId).setBackground(mDefBg);
        }
        mCurrentClickedId = id;
        mButtonsMap.get(mCurrentClickedId).setBackgroundColor(Color.YELLOW);
    }


    public boolean handleCheck(int correctVar) {
        if (mCurrentClickedId == -1) return false;
        if (mCurrentClickedId == mVarId2BtnId.get(correctVar)) {
            mButtonsMap.get(mCurrentClickedId).setBackgroundColor(Color.GREEN);
            return true;
        } else {
            mButtonsMap.get(mCurrentClickedId).setBackgroundColor(Color.RED);
            mButtonsMap.get(mVarId2BtnId.get(correctVar)).setBackgroundColor(Color.GREEN);
            return false;
        }
    }
}
