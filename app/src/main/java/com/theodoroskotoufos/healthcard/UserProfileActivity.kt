package com.theodoroskotoufos.healthcard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.FirebaseDatabase

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        setSupportActionBar(findViewById(R.id.toolbar2))
        findViewById<Toolbar>(R.id.toolbar2).title = title

        val personalID: String = intent.getStringExtra("personalID").toString()
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")
    }
}