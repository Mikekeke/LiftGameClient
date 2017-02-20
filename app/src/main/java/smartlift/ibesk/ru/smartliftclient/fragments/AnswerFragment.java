package smartlift.ibesk.ru.smartliftclient.fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import smartlift.ibesk.ru.smartliftclient.R;
import smartlift.ibesk.ru.smartliftclient.Settings;
import smartlift.ibesk.ru.smartliftclient.views.ListenableImage;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnswerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnswerFragment extends Fragment {
    private static final String TAG = "qq";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ANSWER = "AnswerFragment.ARG_ANSWER";
    private static final String ARG_IMAGE2 = "AnswerFragment.ARG_IMAGE2";

    private String mAnswer = "";
    private String mImage2 = "";
    private ListenableImage mImageOverlay;
    private TextView mAnswerTv;
    private SharedPreferences mPrefs;
    private RequestQueue mQueue;

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
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        mQueue = Volley.newRequestQueue(getActivity());
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
        mAnswerTv.setAlpha(0.0f);
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
        if(TextUtils.isEmpty(mImage2)) {
            mAnswerTv.animate().alpha(1.0f).setDuration(Settings.App.FADE_INT_TIME);
            return;
        }
        
        mImageOverlay = (ListenableImage) getActivity().findViewById(R.id.overlay_image);
        mImageOverlay.setAlpha(0.0f);
        mImageOverlay.setVisibility(View.VISIBLE);
        mImageOverlay.setTextViewToAdjust(mAnswerTv);
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (mImageOverlay != null) {
                        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        String imgUrl = mPrefs.getString(Settings.IMG_URL, "") + "?name=" + mImage2;
                        ImageRequest ir = new ImageRequest(imgUrl, new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap response) {
                                mImageOverlay.setImageBitmap(response);
                                mImageOverlay.animate().alpha(1.0f).setDuration(Settings.App.FADE_INT_TIME);
                                mAnswerTv.animate().alpha(1.0f).setDuration(Settings.App.FADE_INT_TIME);
                            }
                        }, 0, 0, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.ARGB_8888,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        mAnswerTv.animate().alpha(1.0f).setDuration(Settings.App.FADE_INT_TIME);
                                        Log.e(TAG, "onErrorResponse: ", error);
                                    }
                                });
                        mQueue.add(ir);
                    }
                }
            });
        }
    }
}
