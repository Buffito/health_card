package com.theodoroskotoufos.healthcard

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.IOException


class MyProfileActivity : AppCompatActivity() {
    private var personalID: String = ""
    private var fname: String = ""
    private var lname: String = ""
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        personalID = intent.getStringExtra("personalID").toString()

        initButtons()
        initName()
        initPhoto()

    }

    private fun initButtons() {
        findViewById<Button>(R.id.infoButton).setOnClickListener {
            /// send to info activity
            val intent = Intent(this, ProfileInfoActivity::class.java)
            intent.putExtra("personalID", personalID)
            startActivity(intent)
        }

        findViewById<Button>(R.id.viewQrButton).setOnClickListener {
            /// send to view qr activity
            val intent = Intent(this, ViewQrActivity::class.java)
            intent.putExtra("personalID", personalID)
            startActivity(intent)
        }

        findViewById<Button>(R.id.scanQrButton).setOnClickListener {
            /// send to qr scanner
         //   val intent = Intent(applicationContext, BarcodeCaptureActivity::class.java)
          //  startActivity(intent)
        }
    }

    private fun initName(){
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(personalID)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fname =
                    snapshot.child("first name").value.toString().trim()
                lname = snapshot.child("last name").value.toString().trim()
                name = "$fname $lname"
                findViewById<TextView>(R.id.textViewName).text = name

            }

            override fun onCancelled(error: DatabaseError) {
                name = "Null"
                findViewById<TextView>(R.id.textViewName).text = name
            }
        })

    }

    private fun initPhoto(){
        val imagesRef = FirebaseStorage.getInstance().reference.child("images").child(personalID)
        val selfieRef = imagesRef.child("selfie")
        var selfieFile: File? = null
        try {
            selfieFile = File.createTempFile("selfie", ".jpeg")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var selfieBitmap : Bitmap
        val finalSelfieFile = selfieFile
        selfieRef.getFile(selfieFile!!)
            .addOnSuccessListener {
                // Local temp file has been created
                selfieBitmap = BitmapFactory.decodeFile(finalSelfieFile!!.path)
                findViewById<CircleImageView>(R.id.profile_image).setImageBitmap(selfieBitmap)
            }.addOnFailureListener { }
    }

    override fun onResume() {
        super.onResume()
        initButtons()
        initName()
        initPhoto()

    }

}