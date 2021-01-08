package com.theodoroskotoufos.healthcard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.FirebaseDatabase

class MyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setSupportActionBar(findViewById(R.id.toolbar3))
        findViewById<Toolbar>(R.id.toolbar3).title = title

        val personalID: String = intent.getStringExtra("personalID").toString()
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")
        var fname = ""
        var lname = ""
        var name = ""

        fname = databaseRef.child(personalID).child("first name").get().toString()
        lname = databaseRef.child(personalID).child("last name").get().toString()
        name = "$fname $lname"

        findViewById<TextView>(R.id.textViewName).text = name

        findViewById<Button>(R.id.infoButton).setOnClickListener {
            /// send to info activity
            val intent = Intent(this, ProfileInfoActivity::class.java)
            intent.putExtra("personalID",personalID)
            startActivity(intent)
        }

        findViewById<Button>(R.id.viewQrButton).setOnClickListener {
            /// send to view qr activity
            val intent = Intent(this, CreateProfileActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.scanQrButton).setOnClickListener {
            /// send to qr scanner
        }
    }
}