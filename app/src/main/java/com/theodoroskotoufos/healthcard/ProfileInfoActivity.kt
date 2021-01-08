package com.theodoroskotoufos.healthcard

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.FirebaseDatabase

class ProfileInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_info)
        setSupportActionBar(findViewById(R.id.toolbar4))
        findViewById<Toolbar>(R.id.toolbar4).title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")
        val personalID: String = intent.getStringExtra("personalID").toString()
        val myRef = databaseRef.child(personalID)

        findViewById<TextView>(R.id.textViewPersonFirstName).text = myRef.child("first name").get().toString()
        findViewById<TextView>(R.id.textViewPersonLastName).text = myRef.child("last name").get().toString()
        findViewById<TextView>(R.id.textViewDate).text = myRef.child("gender").get().toString()
        findViewById<TextView>(R.id.textViewGender).text = myRef.child("date of birth").get().toString()
        findViewById<TextView>(R.id.textViewCountryISOCode).text = myRef.child("iso code").get().toString()
        findViewById<TextView>(R.id.textViewPersonalID).text = myRef.child("personal id").get().toString()
        findViewById<TextView>(R.id.textViewCardID).text = myRef.child("card id").get().toString()
        findViewById<TextView>(R.id.textViewVaccineName).text = sharedPref.getString("vaccine_name","nothing")
        findViewById<TextView>(R.id.textViewDateVaccine).text = sharedPref.getString("vaccine_date","nothing")

    }
}