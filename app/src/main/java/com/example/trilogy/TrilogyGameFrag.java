package com.example.trilogy;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TrilogyGameFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrilogyGameFrag extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TrilogyGameFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TrilogyGameFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static TrilogyGameFrag newInstance(String param1, String param2) {
        TrilogyGameFrag fragment = new TrilogyGameFrag();
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
        View view = inflater.inflate(R.layout.fragment_trilogy_game, container, false);

        // Games Buttons/Animation
        CardView bdbtn = view.findViewById(R.id.bd);
        CardView nsbtn = view.findViewById(R.id.ns);
        CardView smbtn = view.findViewById(R.id.smbtn);
        CardView gtobtn = view.findViewById(R.id.gto);
        CardView ecbtn = view.findViewById(R.id.ec);
        CardView eimbtn = view.findViewById(R.id.eim);
        CardView worldbtn = view.findViewById(R.id.world);
        CardView wnqbtn = view.findViewById(R.id.wnq);

        int[] ids = {
                R.id.bd, R.id.ns, R.id.sm, R.id.gto,
                R.id.ec, R.id.eim, R.id.world, R.id.wnq
        };

        for (int id : ids) {
            addPressPlayScreenAnimation(view.findViewById(id));
        }

            bdbtn.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("GAME_MODE", "BOMB_DEFUSE");

                Difficultychooser fragment = new Difficultychooser();
                fragment.setArguments(bundle);

                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();

            });
            nsbtn.setOnClickListener(v -> {
                        Intent intent = new Intent(requireActivity(), NumberSequenceGame.class);
                        startActivity(intent);
                    });
//
//
//
//            gtobtn.setOnClickListener(view1 ->
//                startActivity(new Intent(getActivity(), Engchoosegame.class));
//
//            ecbtn.setOnClickListener(view1 ->
//                startActivity(new Intent(getActivity(), Mathchoosegame.class));
//
            eimbtn.setOnClickListener(view1 -> {
                Intent intent = new Intent(requireActivity(), GameActivity.class);
                startActivity(intent);
            });
            worldbtn.setOnClickListener(view1 -> {
                startActivity(new Intent(getActivity(), WordleActivity.class));

                    });
            wnqbtn.setOnClickListener(view1 -> {
                Intent intent = new Intent(requireActivity(), WordMeaningQuiz.class);
                startActivity(intent);
            });




        return view;
    }
    private void addPressPlayScreenAnimation(CardView button) {
        if (button == null) return;

        button.setClickable(true);

        button.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {

                case MotionEvent.ACTION_DOWN:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    v.animate()
                            .scaleX(0.92f)
                            .scaleY(0.92f)
                            .setDuration(80)
                            .start();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    v.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(180)
                            .setInterpolator(new OvershootInterpolator())
                            .start();
                    break;
            }
            return false;
        });
    }

}