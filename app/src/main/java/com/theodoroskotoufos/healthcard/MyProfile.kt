package com.theodoroskotoufos.healthcard

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase

class MyProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")
    }
}