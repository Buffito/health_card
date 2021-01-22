package com.theodoroskotoufos.healthcard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.IOException
import java.util.*

class ProfileInfoActivity : AppCompatActivity() {
    private var personalID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_info)

        personalID = intent.getStringExtra("personalID").toString()
        val activity = this@ProfileInfoActivity
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(personalID)
        val defaultValue = "null"

        initPhoto()

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                findViewById<TextView>(R.id.textViewPersonFirstName).text =
                    snapshot.child("first name").value.toString().trim()
                findViewById<TextView>(R.id.textViewPersonLastName).text =
                    snapshot.child("last name").value.toString().trim()
                findViewById<TextView>(R.id.textViewDate).text =
                    snapshot.child("gender").value.toString().trim()
                findViewById<TextView>(R.id.textViewGender).text =
                    snapshot.child("date of birth").value.toString().trim()
                findViewById<TextView>(R.id.textViewCountry).text =
                    snapshot.child("country").value.toString().trim()
                findViewById<TextView>(R.id.textViewPersonalID).text =
                    snapshot.child("personal id").value.toString().trim()
                findViewById<TextView>(R.id.textViewCardID).text =
                    snapshot.child("card id").value.toString().trim()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        findViewById<TextView>(R.id.textViewVaccineName).text =
            sharedPref.getString("vaccine_name", defaultValue)
        findViewById<TextView>(R.id.textViewDateVaccine).text =
            sharedPref.getString("vaccine_date", defaultValue)
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
                findViewById<CircleImageView>(R.id.profile_image2).setImageBitmap(selfieBitmap)
            }.addOnFailureListener { }
    }
}