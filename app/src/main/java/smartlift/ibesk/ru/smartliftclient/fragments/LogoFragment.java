package smartlift.ibesk.ru.smartliftclient.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import smartlift.ibesk.ru.smartliftclient.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LogoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogoFragment extends Fragment {
    private static final String EXTRA_END_GAME = "LogoFragment.EXTRA_END_GAME";
    private boolean mEndGame = false;


    public static LogoFragment newInstance() {
        LogoFragment fragment = new LogoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static LogoFragment endGameInstance() {
        LogoFragment fragment = new LogoFragment();
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_END_GAME, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEndGame = getArguments().getBoolean(EXTRA_END_GAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_logo, container, false);
//        v.findViewById(R.id.endgame_text).setVisibility(mEndGame ? View.VISIBLE : View.GONE);
        return v;
    }

}
