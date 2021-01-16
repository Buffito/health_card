package com.theodoroskotoufos.healthcard

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class ProfileInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_info)
        setSupportActionBar(findViewById(R.id.toolbar3))
        findViewById<Toolbar>(R.id.toolbar3).title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val personalID: String = intent.getStringExtra("personalID").toString()
        val activity = this@ProfileInfoActivity
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(personalID)
        val defaultValue = "null"

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                findViewById<TextView>(R.id.textViewPersonFirstName).text =
                    snapshot.child("first name").value.toString().trim().toUpperCase(Locale.ROOT)
                findViewById<TextView>(R.id.textViewPersonLastName).text =
                    snapshot.child("last name").value.toString().trim().toUpperCase(Locale.ROOT)
                findViewById<TextView>(R.id.textViewDate).text =
                    snapshot.child("gender").value.toString().trim().toUpperCase(Locale.ROOT)
                findViewById<TextView>(R.id.textViewGender).text =
                    snapshot.child("date of birth").value.toString().trim().toUpperCase(Locale.ROOT)
                findViewById<TextView>(R.id.textViewCountryISOCode).text =
                    snapshot.child("iso code").value.toString().trim().toUpperCase(Locale.ROOT)
                findViewById<TextView>(R.id.textViewPersonalID).text =
                    snapshot.child("personal id").value.toString().trim().toUpperCase(Locale.ROOT)
                findViewById<TextView>(R.id.textViewCardID).text =
                    snapshot.child("card id").value.toString().trim().toUpperCase(Locale.ROOT)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        findViewById<TextView>(R.id.textViewVaccineName).text =
            sharedPref.getString("vaccine_name", defaultValue)!!
                .toUpperCase(
                    Locale.ROOT
                )
        findViewById<TextView>(R.id.textViewDateVaccine).text =
            sharedPref.getString("vaccine_date", defaultValue)!!
                .toUpperCase(Locale.ROOT)
    }
}