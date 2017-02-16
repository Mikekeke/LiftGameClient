package smartlift.ibesk.ru.smartliftclient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import smartlift.ibesk.ru.smartliftclient.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnswerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnswerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ANSWER = "AnswerFragment.ARG_ANSWER";
    private static final String ARG_IMAGE2 = "AnswerFragment.ARG_IMAGE2";

    private String mAnswer = "";
    private String mImage2 = "";
    private ImageView mImageOverlay;
    private TextView mAnswerTv;

    public AnswerFragment() {
        // Required empty public constructor
    }

    public static AnswerFragment newInstance(String answer, String image2) {
        AnswerFragment fragment = new AnswerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ANSWER, answer);
        args.putString(ARG_IMAGE2, image2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAnswer = getArguments().getString(ARG_ANSWER);
            mImage2 = getArguments().getString(ARG_IMAGE2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_answer, container, false);
        mAnswerTv = (TextView) v.findViewById(R.id.answer_tv);
        mAnswerTv.setText(mAnswer);
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mImageOverlay != null) {
            mImageOverlay.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("qq", "onViewCreated: ");
        if(TextUtils.isEmpty(mImage2)) return;
        
        DisplayMetrics dm = getActivity().getResources().getDisplayMetrics();
        final int dpi = (int) dm.density;
        mImageOverlay = (ImageView) getActivity().findViewById(R.id.overlay_image);
        mImageOverlay.setImageResource(R.drawable.test2);
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Log.d("qq", "onGlobalLayout: ");
                    if (mImageOverlay != null) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        //                        mImageOverlay.setMinimumWidth(side);
                        mImageOverlay.setTranslationY(-20f * dpi);
                        //                        mImageOverlay.invalidate();
                        int paddingLeft = (mImageOverlay.getMeasuredWidth() + 20 + 80) * dpi;
                        mAnswerTv.setPadding(paddingLeft, 100 * dpi, 15 * dpi, 0);
                        mImageOverlay.setVisibility(View.VISIBLE);

                    }
                }
            });
        }
    }
}
