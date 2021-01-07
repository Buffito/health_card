package com.theodoroskotoufos.healthcard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.FirebaseDatabase

class MyProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setSupportActionBar(findViewById(R.id.toolbar3))
        findViewById<Toolbar>(R.id.toolbar3).title = title

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")

        findViewById<Button>(R.id.infoButton).setOnClickListener {
            /// send to info activity
            val intent = Intent(this, ProfileInfoActivity::class.java)
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