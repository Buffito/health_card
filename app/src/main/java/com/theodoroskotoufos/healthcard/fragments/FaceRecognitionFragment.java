package com.theodoroskotoufos.healthcard.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theodoroskotoufos.healthcard.FaceDetection.Box;
import com.theodoroskotoufos.healthcard.FaceDetection.MTCNN;
import com.theodoroskotoufos.healthcard.FaceRecognition.FaceNet;
import com.theodoroskotoufos.healthcard.MainActivity;
import com.theodoroskotoufos.healthcard.R;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public class FaceRecognitionFragment extends Fragment {

    private Bitmap selfieBitmap, cardBitmap;
    private String gender = "";
    private String country = "";
    private ArrayList<String> countries;

    public FaceRecognitionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_face_recognition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        countries = new ArrayList<String>() {
            {
                add("China");
                add("Indonesia");
                add("Japan");
                add("Philippines");
                add("Vietnam");
                add("Thailand");
                add("Myanmar");
                add("South Korea");
                add("Malaysia");
                add("North Korea");
                add("Cambodia");
                add("Laos");
                add("Singapore");
                add("Mongolia");
                add("Timor-Leste");
                add("Brunei");
            }
        };

        try {
            MasterKey mainKey = new MasterKey.Builder(requireActivity())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            SharedPreferences sharedPref = EncryptedSharedPreferences.create(
                    requireActivity(),
                    "sharedPrefsFile",
                    mainKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            getPicturesToBitmap(sharedPref);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

    }

    private void getPicturesToBitmap(SharedPreferences sharedPref) {
        String personalID = sharedPref.getString("personalID", "").trim();
        gender = sharedPref.getString("gender", "").trim();
        country = sharedPref.getString("country", "").trim();

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

        new Handler().postDelayed(this::faceRecognition, 500);
    }

    private void faceRecognition() {
        MTCNN mtcnn = new MTCNN(requireActivity().getAssets());

        Bitmap face1 = cropFace(selfieBitmap, mtcnn);
        Bitmap face2 = cropFace(cardBitmap, mtcnn);

        mtcnn.close();

        FaceNet facenet = null;
        try {
            facenet = new FaceNet(requireActivity().getAssets());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getContext(), MainActivity.class);
        if (face1 != null && face2 != null) {
            assert facenet != null;
            double score = facenet.getSimilarityScore(face1, face2);

            double targetScore;

            switch (gender) {
                case "Male":
                    targetScore = 24.00;
                    if (countries.contains(country))
                        targetScore = 15.00;

                    checkScore(targetScore, score, intent);

                    break;

                case "Female":
                    targetScore = 17.00;
                    if (countries.contains(country))
                        targetScore = 8.00;

                    checkScore(targetScore, score, intent);

                    break;
                default:
                    return;
            }

        } else {
            intent.putExtra("flag", "create");
            Toast.makeText(getContext(), "Error occurred while creating profile.", Toast.LENGTH_LONG).show();
        }
        startActivity(intent);

        assert facenet != null;
        facenet.close();
    }

    private void checkScore(Double targetScore, Double score, Intent intent) {
        if (score < targetScore)
            intent.putExtra("flag", "profile");
        else
            intent.putExtra("flag", "create");
    }


    private Bitmap cropFace(Bitmap bitmap, MTCNN mtcnn) {
        Bitmap croppedBitmap = null;
        try {
            Vector<Box> boxes = mtcnn.detectFaces(bitmap, 10);

            int x = boxes.get(0).left();
            int y = boxes.get(0).top();
            int width = boxes.get(0).width();
            int height = boxes.get(0).height();


            if (y + height >= bitmap.getHeight())
                height -= (y + height) - (bitmap.getHeight() - 1);
            if (x + width >= bitmap.getWidth())
                width -= (x + width) - (bitmap.getWidth() - 1);

            croppedBitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return croppedBitmap;
    }
}