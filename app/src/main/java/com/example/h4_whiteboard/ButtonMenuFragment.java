package com.example.h4_whiteboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ButtonMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ButtonMenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Button createButton;
    View view;
    RelativeLayout imageLayout;

    public ButtonMenuFragment() {
        // Required empty public constructor
    }

    Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ButtonMenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ButtonMenuFragment newInstance(String param1, String param2) {
        ButtonMenuFragment fragment = new ButtonMenuFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_button_menu, container, false);
        createButton = (Button) view.findViewById(R.id.createButton);
        createButton.setOnClickListener(OnClickCreateImage());

        View mainView = inflater.inflate(R.layout.activity_main,container, false);
        imageLayout = (RelativeLayout)mainView.findViewById(R.id.imagesContainer);
        return view;
    }

    private View.OnClickListener OnClickCreateImage() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context,ButtonMenuFragment.class);
                ImageView iv = new ImageView(getContext());
                iv.setImageResource(R.drawable.img001);
                AddImageToView(iv, 600, 800);
            }
        };
    }

    public void AddImageToView(ImageView image, int width, int height) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.setMargins(2, 2, 2, 2);
        image.setLayoutParams(params);
        image.setX(300);
        image.setY(300);
        imageLayout.addView(image);
//        int dsada = imageLayout.getChildCount();
//        image.setOnTouchListener(imageDraggable());
    }


//    private View.OnTouchListener imageDraggable() {
//        return new View.OnTouchListener() {
//            float x, y;
//            float dx, dy;
//            int xDelta, yDelta;
//
//            @SuppressLint("ClickableViewAccessibility")
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//
//                final int x = (int) event.getRawX();
//                final int y = (int) event.getRawY();
//
//                switch (event.getAction() & MotionEvent.ACTION_MASK) {
//
//                    case MotionEvent.ACTION_DOWN:
//                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
//                                view.getLayoutParams();
//
//                        xDelta = x - lParams.leftMargin;
//                        yDelta = y - lParams.topMargin;
//                        break;
//
//                    case MotionEvent.ACTION_MOVE:
//                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
//                                .getLayoutParams();
//                        layoutParams.leftMargin = x - xDelta;
//                        layoutParams.topMargin = y - yDelta;
//                        layoutParams.rightMargin = 0;
//                        layoutParams.bottomMargin = 0;
//                        view.setLayoutParams(layoutParams);
//                        break;
//                }
//
//                R.layout.fragment_button_menu.invalidate();
//                return true;
//            }
//        };
//    }

}