package com.theodoroskotoufos.healthcard.FaceRecognition;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class FaceNet {
    private static final String MODEL_PATH = "facenet.tflite";

    private static final int BATCH_SIZE = 1;
    private static final int IMAGE_HEIGHT = 160;
    private static final int IMAGE_WIDTH = 160;
    private static final int NUM_CHANNELS = 3;
    private static final int NUM_BYTES_PER_CHANNEL = 4;
    private static final int EMBEDDING_SIZE = 512;

    private final int[] intValues = new int[IMAGE_HEIGHT * IMAGE_WIDTH];
    private final ByteBuffer imgData;

    private MappedByteBuffer tfliteModel;
    private Interpreter tflite;
    private final Interpreter.Options tfliteOptions = new Interpreter.Options();

    public FaceNet(AssetManager assetManager) throws IOException {
        tfliteModel = loadModelFile(assetManager);
        tflite = new Interpreter(tfliteModel, tfliteOptions);
        imgData = ByteBuffer.allocateDirect(
                BATCH_SIZE
                        * IMAGE_HEIGHT
                        * IMAGE_WIDTH
                        * NUM_CHANNELS
                        * NUM_BYTES_PER_CHANNEL);
        imgData.order(ByteOrder.nativeOrder());
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(MODEL_PATH);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // Convert the image to floating point.
        int pixel = 0;
        for (int i = 0; i < IMAGE_HEIGHT; ++i) {
            for (int j = 0; j < IMAGE_WIDTH; ++j) {
                final int val = intValues[pixel++];
                addPixelValue(val);
            }
        }
    }

    private void addPixelValue(int pixelValue) {
        imgData.putFloat(((pixelValue >> 16) & 0xFF) / 255.0f);
        imgData.putFloat(((pixelValue >> 8) & 0xFF) / 255.0f);
        imgData.putFloat((pixelValue & 0xFF) / 255.0f);
    }

    private Bitmap resizedBitmap(Bitmap bitmap, int height, int width) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private float[][] run(Bitmap bitmap) {
        bitmap = resizedBitmap(bitmap, IMAGE_HEIGHT, IMAGE_WIDTH);
        convertBitmapToByteBuffer(bitmap);

        float[][] embeddings = new float[1][512];
        tflite.run(imgData, embeddings);

        return embeddings;
    }

    public double getSimilarityScore(Bitmap face1, Bitmap face2) {
        float[][] face1_embedding = run(face1);
        float[][] face2_embedding = run(face2);

        double distance = 0.0;
        for (int i = 0; i < EMBEDDING_SIZE; i++) {
            distance += (face1_embedding[0][i] - face2_embedding[0][i]) * (face1_embedding[0][i] - face2_embedding[0][i]);
        }
        distance = Math.sqrt(distance);

        return distance;
    }

    public void close() {
        if (tflite != null) {
            tflite.close();
            tflite = null;
        }
        tfliteModel = null;
    }
}
