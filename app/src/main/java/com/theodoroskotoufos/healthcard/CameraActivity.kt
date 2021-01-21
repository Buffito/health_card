package com.theodoroskotoufos.healthcard

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CameraActivity : AppCompatActivity() {

    private var personalID: String = ""
    private var camera: String = ""
    private var gender: String = ""
    private var country: String = ""


    private lateinit var storage: FirebaseStorage
    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    lateinit var preview: PreviewView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        storage = Firebase.storage

        personalID = intent.getStringExtra("personalID").toString()
        camera = intent.getStringExtra("camera").toString()
        gender = intent.getStringExtra("gender").toString()
        country = intent.getStringExtra("country").toString()

        val selfieHint = "Please take a selfie."
        val cardFrontHint = "Please take a photo of your card. (Front Side)"
        val cardBackHint = "Please take a photo of your card. (Back Side)"

        preview = findViewById(R.id.viewFinder)

        when (camera) {
            "selfie" -> findViewById<TextView>(R.id.hintView).text = selfieHint
            "card_front" -> findViewById<TextView>(R.id.hintView).text = cardFrontHint
            "card_back" -> findViewById<TextView>(R.id.hintView).text = cardBackHint
        }


        // Set up the listener for take photo button
        findViewById<ImageButton>(R.id.camera_capture_button).setOnClickListener { takePhoto() }


        cameraExecutor = Executors.newSingleThreadExecutor()

        startCamera()


    }

    private fun takePhoto() {
        val storageRef = storage.reference
        val imagesRef: StorageReference?
        imagesRef = when (camera) {
            "selfie" -> storageRef.child("images").child(personalID).child("selfie")
            "card_front" -> storageRef.child("images").child(personalID).child("card front photo")
            else -> storageRef.child("images").child(personalID).child("card back photo")
        }

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Photo Upload")
        builder.setMessage("Uploading ...")


        imageCapture.takePicture(ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    preview.isDrawingCacheEnabled = true
                    preview.buildDrawingCache()
                    val baos = ByteArrayOutputStream()
                    preview.bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    val uploadTask = imagesRef.putBytes(data)
                    builder.show()
                    uploadTask.addOnFailureListener {
                        // Handle unsuccessful uploads
                    }.addOnSuccessListener {
                        // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                        // ...
                        when (camera) {
                            "selfie" -> {
                                val intent = Intent(applicationContext, CameraActivity::class.java)
                                intent.putExtra("personalID", personalID)
                                intent.putExtra("gender", gender)
                                intent.putExtra("country", country)
                                intent.putExtra("camera", "card_front")

                                startActivity(intent)
                            }
                            "card_front" -> {
                                val intent = Intent(applicationContext, CameraActivity::class.java)
                                intent.putExtra("personalID", personalID)
                                intent.putExtra("gender", gender)
                                intent.putExtra("country", country)
                                intent.putExtra("camera", "card_back")

                                startActivity(intent)
                            }
                            "card_back" -> {
                                val intent = Intent(applicationContext, Facerecognition::class.java)
                                intent.putExtra("personalID", personalID)
                                intent.putExtra("gender", gender)
                                intent.putExtra("country", country)

                                startActivity(intent)
                            }
                        }
                    }
                }
            })

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(findViewById<PreviewView>(R.id.viewFinder).surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select camera as a default
            val cameraSelector: CameraSelector = if (camera == "selfie")
                CameraSelector.DEFAULT_FRONT_CAMERA
            else
                CameraSelector.DEFAULT_BACK_CAMERA



            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e("CameraXBasic", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }


}



