package com.example.h4_whiteboard;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    ViewGroup mainLayout;
    RelativeLayout imageLayout;
    FrameLayout fragmentButtonLayout;
    ImageView imageView;
    Button createBtn;
    Button takePicBtn;
    Button removeAllBtn;
    Button openButtonsMenu;
    Bitmap picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.relLayout);
        imageLayout = findViewById(R.id.imagesContainer);

        fragmentButtonLayout = findViewById(R.id.fragmentButtonsMenu);
        openButtonsMenu = findViewById(R.id.openButtonsMenuFragment);
        openButtonsMenu.setOnClickListener(GetImagesFromApi());
//        openButtonsMenu.setOnClickListener(OpenButtonsMenuFragment());

        imageView = findViewById(R.id.iv_Test);
        imageView.setOnTouchListener(imageDraggable());

//        createBtn = findViewById(R.id.createButton);
//        createBtn.setOnClickListener(CreateImage());
//        takePicBtn = findViewById(R.id.takePictureButton);
//        takePicBtn.setOnClickListener(dispatchTakePictureIntent());
//        removeAllBtn = findViewById(R.id.removeAllButton);
//        removeAllBtn.setOnClickListener(RemoveAllImages());
    }

    /**
     * Creates a new image and puts it into the layout
     *
     * @return returns the onclicklistener to the createBtn.onCLick()
     */
    private OnClickListener CreateImage() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView iv = new ImageView(MainActivity.this);
                iv.setImageResource(R.drawable.img001);
                AddImageToView(iv, 300, 500);
            }
        };
    }

    private OnClickListener RemoveAllImages() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageLayout.removeAllViews();
            }
        };
    }

    private OnClickListener OpenButtonsMenuFragment() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentButtonsMenu,
                                ButtonMenuFragment.class,
                                null).commit();

            }
        };
    }


    private OnClickListener dispatchTakePictureIntent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureActivityResultLauncher.launch(takePictureIntent);
            }
        };
    }

    ActivityResultLauncher<Intent> takePictureActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        picture = GetImage(result.getData());
                        // Set bitmap to ImageView
                        ImageView imageV = new ImageView(MainActivity.this);
                        imageV.setImageBitmap(picture);
                        AddImageToView(imageV, 300, 300);
                    }
                }
            });

    public Bitmap GetImage(Intent data) {
        Bundle extras = data.getExtras();
        return (Bitmap) extras.get("data");
    }


    public void AddImageToView(ImageView image, int width, int height) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.setMargins(2, 2, 2, 2);
        image.setLayoutParams(params);
        imageLayout.addView(image);
        image.setOnTouchListener(imageDraggable());
    }

    private OnTouchListener imageDraggable() {
        return new OnTouchListener() {
            float x, y;
            float dx, dy;
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


    private OnClickListener GetImagesFromApi() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://10.108.162.64:5284/images") //Machine local ip!
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String temp = response.body().string();


                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    byte[] decodeString = Base64.decode(temp, Base64.DEFAULT);
                                    Bitmap picture = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
                                    ImageView image = new ImageView(MainActivity.this);
                                    image.setImageBitmap(picture);
                                    AddImageToView(image, 300, 300);
                                }
                            });
                        }
                    }
                });
            }
        };
    }
}