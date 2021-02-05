package com.theodoroskotoufos.healthcard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theodoroskotoufos.healthcard.FaceDetection.Box;
import com.theodoroskotoufos.healthcard.FaceDetection.MTCNN;
import com.theodoroskotoufos.healthcard.FaceRecognition.FaceNet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class Facerecognition extends AppCompatActivity {

    private Bitmap selfieBitmap, cardBitmap;
    private String gender = "", personalID = "", country = "";
    private ProgressBar progressBar;
    private ArrayList<String> countries;
    AsyncTask<?, ?, ?> runningTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facerecognition);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(200);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        personalID = sharedPref.getString("personalID", "");
        gender = sharedPref.getString("gender", "");
        country = sharedPref.getString("country", "");

        countries.add("China");
        countries.add("Indonesia");
        countries.add("Japan");
        countries.add("Philippines");
        countries.add("Vietnam");
        countries.add("Thailand");
        countries.add("Myanmar");
        countries.add("South Korea");
        countries.add("Malaysia");
        countries.add("North Korea");
        countries.add("Cambodia");
        countries.add("Laos");
        countries.add("Singapore");
        countries.add("Mongolia");
        countries.add("Timor-Leste");
        countries.add("Brunei");

        if (runningTask != null)
            runningTask.cancel(true);
        runningTask = new FaceOperation();
        runningTask.execute();
    }

    private void doSomething(){
        init();
        new Handler().postDelayed(this::faceRecognition, 1000);
    }

    private void init(){
        StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("images").child(personalID);
        StorageReference selfieRef = imagesRef.child("selfie");
        StorageReference cardRef = imagesRef.child("card photo");


        File selfieFile = null;
        try {
            selfieFile = File.createTempFile("selfie", ".jpeg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        File finalSelfieFile = selfieFile;
        selfieRef.getFile(selfieFile).addOnSuccessListener(taskSnapshot -> {
            // Local temp file has been created
            selfieBitmap = BitmapFactory.decodeFile(finalSelfieFile.getPath());
        }).addOnFailureListener(exception -> {
            // Handle any errors
        });

        File cardFile = null;
        try {
            cardFile = File.createTempFile("card", ".jpeg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        File finalCardFile = cardFile;
        cardRef.getFile(cardFile).addOnSuccessListener(taskSnapshot -> {
            // Local temp file has been created
            cardBitmap = BitmapFactory.decodeFile(finalCardFile.getPath());
        }).addOnFailureListener(exception -> {
            // Handle any errors
        });
    }

    private void faceRecognition() {
        MTCNN mtcnn = new MTCNN(getAssets());

        Bitmap face1 = cropFace(selfieBitmap, mtcnn);
        Bitmap face2 = cropFace(cardBitmap, mtcnn);

        mtcnn.close();

        FaceNet facenet = null;
        try {
            facenet = new FaceNet(getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (face1 != null && face2 != null) {
            assert facenet != null;
            double score = facenet.getSimilarityScore(face1, face2);
            Log.i("score", String.valueOf(score));

            Intent intent = null;
            if (gender.equals("Male")) {
                if (countries.contains(country)) {
                    if (score < 15.00) {
                      //  intent = new Intent(this, MyProfileActivity.class);
                      //  intent.putExtra("personalID", personalID);
                    } else {
                      //  intent = new Intent(this, CreateProfileActivity.class);
                        Toast.makeText(getApplicationContext(), "Error occurred while creating profile.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (score < 24.00) {
                       // intent = new Intent(this, MyProfileActivity.class);
                        intent.putExtra("personalID", personalID);
                    } else {
                       // intent = new Intent(this, CreateProfileActivity.class);
                        Toast.makeText(getApplicationContext(), "Error occurred while creating profile.", Toast.LENGTH_LONG).show();
                    }
                }

            } else if (gender.equals("Female")) {
                if (countries.contains(country)) {
                    if (score < 8.00) {
                      //  intent = new Intent(this, MyProfileActivity.class);
                      //  intent.putExtra("personalID", personalID);
                    } else {
                      //  intent = new Intent(this, CreateProfileActivity.class);
                        Toast.makeText(getApplicationContext(), "Error occurred while creating profile.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (score < 17.00) {
                      //  intent = new Intent(this, MyProfileActivity.class);
                      //  intent.putExtra("personalID", personalID);
                    } else {
                      //  intent = new Intent(this, CreateProfileActivity.class);
                        Toast.makeText(getApplicationContext(), "Error occurred while creating profile.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            startActivity(intent);

        } else {
            Log.i("HERE", "how about no");
        }

        assert facenet != null;
        facenet.close();
    }

    private Bitmap cropFace(Bitmap bitmap, MTCNN mtcnn) {
        Bitmap croppedBitmap = null;
        try {
            Vector<Box> boxes = mtcnn.detectFaces(bitmap, 10);

            Log.i("MTCNN", "No. of faces detected: " + boxes.size());

            int left = boxes.get(0).left();
            int top = boxes.get(0).top();

            int x = boxes.get(0).left();
            int y = boxes.get(0).top();
            int width = boxes.get(0).width();
            int height = boxes.get(0).height();


            if (y + height >= bitmap.getHeight())
                height -= (y + height) - (bitmap.getHeight() - 1);
            if (x + width >= bitmap.getWidth())
                width -= (x + width) - (bitmap.getWidth() - 1);

            Log.i("MTCNN", "Final x: " + (x + width));
            Log.i("MTCNN", "Width: " + bitmap.getWidth());
            Log.i("MTCNN", "Final y: " + (y + width));
            Log.i("MTCNN", "Height: " + bitmap.getWidth());

            croppedBitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return croppedBitmap;
    }

    private final class FaceOperation extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Guaranteed to run on the UI thread
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            doSomething();
            return "Executed";
        }


        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);

        }
    }

}