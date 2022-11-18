package com.example.h4_whiteboard;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ApiFetcher fetcher;

    ViewGroup mainLayout;
    RelativeLayout imageLayout;
    ImageView imageView;
    Button createBtn;
    Button takePicBtn;
    Button removeAllBtn;
    Bitmap picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetcher = new ApiFetcher();

        mainLayout = findViewById(R.id.relLayout);
        imageLayout = findViewById(R.id.imagesContainer);

        imageView = findViewById(R.id.iv_Test);
        imageView.setOnTouchListener(imageDraggable());

        createBtn = findViewById(R.id.createButton);
        createBtn.setOnClickListener(GetImagesFromApi());
        takePicBtn = findViewById(R.id.takePictureButton);
        takePicBtn.setOnClickListener(dispatchTakePictureIntent());
        removeAllBtn = findViewById(R.id.removeAllButton);
        removeAllBtn.setOnClickListener(RemoveAllImages());
    }

    /***
     * Removes all images put into the {@link #imageLayout}, used for a button's {@link OnClickListener}
     * @return returns an {@link OnClickListener}, used for a {@link Button}
     */
    private OnClickListener RemoveAllImages() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageLayout.removeAllViews();
            }
        };
    }

    private OnClickListener dispatchTakePictureIntent() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create an intent, to open the camera, refer to the ActivityResultLauncher's onActivityResult() for what we do with the picture
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePictureActivityResultLauncher.launch(takePictureIntent);
            }
        };
    }

    /***
     * Adds a new ImageView to the {@link #imageLayout}, with a {@link OnClickListener} to the {@link #imageDraggable()} method
     * @param bitmap the bitmap that will be converted into an ImageView
     * @param width Width of the ImageView
     * @param height Height of the ImageView
     */
    public void AddImageToView(Bitmap bitmap, int width, int height) {
        ImageView image = new ImageView(MainActivity.this);
        image.setImageBitmap(bitmap);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.setMargins(2, 2, 2, 2);
        image.setLayoutParams(params);

        imageLayout.addView(image);
        image.setOnTouchListener(imageDraggable());
    }

    /***
     * Fetches images from the api, displays them in the {@link #imageLayout} and sets their {@link OnClickListener} to {@link #imageDraggable()}
     * @return returns an {@link OnClickListener}, used for a {@link Button}
     */
    private OnClickListener GetImagesFromApi() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap[] bitmaps = fetcher.GetImagesFromApi();
                if (bitmaps != null && bitmaps.length > 0) {
                    for (int i = 0; i < bitmaps.length; i++) {
                        AddImageToView(bitmaps[i], 250, 250);
                    }
                }
            }
        };
    }

    /***
     * Makes whatever {@link View} this is attached to, draggable
     * @return Returns an {@link OnTouchListener}
     */
    private OnTouchListener imageDraggable() {
        return new OnTouchListener() {
            float x, y;
            float dx, dy;
            int xDelta, yDelta;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                //view win this context, is our current ImageView, that is being pressed/moved
                //get x and y of whatever was touched
                final int x = (int) event.getRawX();
                final int y = (int) event.getRawY();

                //Switch on current action
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    //If mouse is held down
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
                        //These don't seem to effect the draggability of the app
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



    //Non-deprecated way for StartActivityForResult()
    ActivityResultLauncher<Intent> takePictureActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        picture = GetImage(result.getData());

                        //Ask if the user wants to save the image to the API
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Save Image to Api?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();

                        AddImageToView(picture, 300, 300);
                    }
                }
            });

    /**
     * Helping method for {@link #takePictureActivityResultLauncher}, returns a bitmap from the data param
     * @param data
     * @return
     */
    public Bitmap GetImage(Intent data) {
        Bundle extras = data.getExtras();
        return (Bitmap) extras.get("data");
    }

    //Create a listener, that listens on the "Save image to api?" dialog box
    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if(fetcher.SendImageToApi(picture)){
                        Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Something went wrong with saving", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };
}
