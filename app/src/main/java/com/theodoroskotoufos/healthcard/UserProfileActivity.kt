package com.theodoroskotoufos.healthcard

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.FirebaseDatabase

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        setSupportActionBar(findViewById(R.id.toolbar2))
        findViewById<Toolbar>(R.id.toolbar2).title = title


        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")
        val personalID: String = intent.getStringExtra("personalID").toString()
        val myRef = databaseRef.child(personalID)

        findViewById<TextView>(R.id.textPersonFirstName).text =
            myRef.child("first name").get().toString()
        findViewById<TextView>(R.id.textPersonLastName).text =
            myRef.child("last name").get().toString()
        findViewById<TextView>(R.id.textDate).text =
            myRef.child("gender").get().toString()
        findViewById<TextView>(R.id.textGender).text =
            myRef.child("date of birth").get().toString()
        findViewById<TextView>(R.id.textCountryISOCode).text =
            myRef.child("iso code").get().toString()
        findViewById<TextView>(R.id.textPersonalID).text =
            myRef.child("personal id").get().toString()
        findViewById<TextView>(R.id.textCardID).text =
            myRef.child("card id").get().toString()
    }
}