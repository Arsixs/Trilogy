package com.example.trilogy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayscreenFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayscreenFrag extends Fragment {

    // Math
    int mathLevel, mathXP;

    // English
    int englishLevel, englishXP;

    // Science
    int scienceLevel, scienceXP;

    ProgressBar mathProgress, englishProgress, scienceProgress;
    TextView mathlvl, mathprog, englvl, engprog, sclvl, scprog;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlayscreenFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayscreenFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayscreenFrag newInstance(String param1, String param2) {
        PlayscreenFrag fragment = new PlayscreenFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_playscreen, container, false);

        // Progress bars
        mathProgress = view.findViewById(R.id.mathProgress);
        englishProgress = view.findViewById(R.id.englishprogress);
        scienceProgress = view.findViewById(R.id.scienceprogress);

        // TextViews
        mathlvl = view.findViewById(R.id.mathlvl);
        mathprog = view.findViewById(R.id.mathprog);

        englvl = view.findViewById(R.id.englvl);
        engprog = view.findViewById(R.id.engprog);

        sclvl = view.findViewById(R.id.sclvl);
        scprog = view.findViewById(R.id.scprog);

        // Subject Buttons/Animation
        ImageButton englishbtn = view.findViewById(R.id.englishbtn);
        ImageButton mathbtn = view.findViewById(R.id.mathbtn);
        ImageButton sciencebtn = view.findViewById(R.id.sciencebtn);

        addPressPlayScreenAnimation(englishbtn);
        addPressPlayScreenAnimation(mathbtn);
        addPressPlayScreenAnimation(sciencebtn);

        // Games Buttons



        englishbtn.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), Engchoosegame.class)));

        mathbtn.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), Mathchoosegame.class)));

        sciencebtn.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), Scichoosegame.class)));

        loadProgress();
        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProgress();
        updateUI();
    }

    private void loadProgress() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("levels", Context.MODE_PRIVATE);

        mathLevel = prefs.getInt("math_level", 1);
        mathXP = prefs.getInt("math_xp", 0);

        englishLevel = prefs.getInt("english_level", 1);
        englishXP = prefs.getInt("english_xp", 0);

        scienceLevel = prefs.getInt("science_level", 1);
        scienceXP = prefs.getInt("science_xp", 0);
    }

    private void updateUI() {
        mathProgress.setMax(100);
        englishProgress.setMax(100);
        scienceProgress.setMax(100);

        mathProgress.setProgress(mathXP);
        mathlvl.setText("Lvl " + mathLevel);
        mathprog.setText(mathXP + "%");

        englishProgress.setProgress(englishXP);
        englvl.setText("Lvl " + englishLevel);
        engprog.setText(englishXP + "%");

        scienceProgress.setProgress(scienceXP);
        sclvl.setText("Lvl " + scienceLevel);
        scprog.setText(scienceXP + "%");
    }

    // Press animation
    private void addPressPlayScreenAnimation(ImageButton button) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.92f).scaleY(0.92f).setDuration(80).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .setInterpolator(new OvershootInterpolator())
                            .start();
                    break;
            }
            return false;
        });
    }
}