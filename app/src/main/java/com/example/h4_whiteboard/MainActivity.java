package com.example.h4_whiteboard;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ViewGroup mainLayout;
    LinearLayout imageLayout;
    ImageView imageView;
    Button createBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.relLayout);

        imageLayout = findViewById(R.id.imagesContainer);

        imageView = findViewById(R.id.iv_Test);
        imageView.setOnTouchListener(onTouchListener());

        createBtn = findViewById(R.id.createButton);
        createBtn.setOnClickListener(CreateImage());
    }

    private OnClickListener CreateImage(){
        return new View.OnClickListener(){
            public void onClick(View v){
                ImageView iv = new ImageView(MainActivity.this);
                iv.setImageResource(R.drawable.img001);

                AddView(iv, 300, 500);
            }
        };
    }
    public void AddView(ImageView image, int width, int height){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
        params.setMargins(2,2,2,2);

        image.setLayoutParams(params);
        imageLayout.addView(image);
        image.setOnTouchListener(onTouchListener());
    }

    private OnTouchListener onTouchListener() {
        return new OnTouchListener() {
            float x,y;
            float dx,dy;
            int xDelta, yDelta;
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)
                                view.getLayoutParams();

                        xDelta = x - lParams.leftMargin;
                        yDelta = y - lParams.topMargin;
                        break;

                    case MotionEvent.ACTION_UP:
                        Toast.makeText(MainActivity.this,
                                        "I'm here!", Toast.LENGTH_SHORT)
                                .show();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                                .getLayoutParams();
                        layoutParams.leftMargin = x - xDelta;
                        layoutParams.topMargin = y - yDelta;
                        layoutParams.rightMargin = 0;
                        layoutParams.bottomMargin = 0;
                        view.setLayoutParams(layoutParams);
                        break;
                }

                mainLayout.invalidate();
                return true;
            }
        };
    }
}