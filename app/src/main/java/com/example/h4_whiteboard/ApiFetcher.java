package com.example.h4_whiteboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Base64;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiFetcher {

    Bitmap[] images;
    boolean result;
    String url = "http://10.108.162.48:5284";

    /***
     * Uses a {@link OkHttpClient} to fetch a base64 encoded string and splits it into a {@link Bitmap[]}
     * @return Returns an array of bitmaps collected from the server
     */
    public Bitmap[] GetImagesFromApi() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url +"/images") //Machine local ip
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    if (response.isSuccessful()) {
                        String temp = response.body().string();

                        String[] encodedImages = temp.split(",");
                        //Initialize the array with the length of encodedImages, so that no null reference exception can occur
                        images = new Bitmap[encodedImages.length];

                        for (int i = 0; i < encodedImages.length; i++) {
                            byte[] decodeString = Base64.decode(encodedImages[i], Base64.DEFAULT);
                            Bitmap picture = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
                            images[i] = picture;
                        }
                    }
                } catch (IOException | ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });
        return images;
    }

    /***
     * Encodes in base64 and sends a bitmap, to the api
     * @param bitmap the bitmap you wish to send to the api
     * @return returns whether or not the operation was succesfull
     */
    public boolean SendImageToApi(Bitmap bitmap) {
        result = false;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder().add("base64Image", encoded).build();

        Request request = new Request.Builder()
                .url(url + "/Images/saveimages")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()){
                    result = true;
                }
            }
        });

        return result;
    }
}
