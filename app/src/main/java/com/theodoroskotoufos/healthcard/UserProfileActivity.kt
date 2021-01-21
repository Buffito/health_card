package com.theodoroskotoufos.healthcard

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        setSupportActionBar(findViewById(R.id.toolbar4))
        findViewById<Toolbar>(R.id.toolbar4).title = title

        val personalID: String = intent.getStringExtra("personalID").toString()
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(personalID)


        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                findViewById<TextView>(R.id.textPersonFirstName).text =
                    snapshot.child("first name").value.toString().trim().toUpperCase(Locale.ROOT)
                findViewById<TextView>(R.id.textPersonLastName).text =
                    snapshot.child("last name").value.toString().trim().toUpperCase(Locale.ROOT)
                findViewById<TextView>(R.id.textDate).text =
                    snapshot.child("gender").value.toString().trim().toUpperCase(Locale.ROOT)
                findViewById<TextView>(R.id.textGender).text =
                    snapshot.child("date of birth").value.toString().trim().toUpperCase(Locale.ROOT)
                findViewById<TextView>(R.id.textCountry).text =
                    snapshot.child("country").value.toString().trim().toUpperCase(Locale.ROOT)
                findViewById<TextView>(R.id.textPersonalID).text =
                    snapshot.child("personal id").value.toString().trim().toUpperCase(Locale.ROOT)
                findViewById<TextView>(R.id.textCardID).text =
                    snapshot.child("card id").value.toString().trim().toUpperCase(Locale.ROOT)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}