package com.theodoroskotoufos.healthcard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class MyProfileActivity : AppCompatActivity() {
    private var personalID: String = ""
    private var fname: String = ""
    private var lname: String = ""
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setSupportActionBar(findViewById(R.id.toolbar2))
        findViewById<Toolbar>(R.id.toolbar2).title = title

        personalID = intent.getStringExtra("personalID").toString()
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(personalID)


        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fname =
                    snapshot.child("first name").value.toString().trim().toUpperCase(Locale.ROOT)
                lname = snapshot.child("last name").value.toString().trim().toUpperCase(Locale.ROOT)
                name = "$fname $lname"

                findViewById<TextView>(R.id.textViewName).text = name

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


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
            val intent = Intent(applicationContext, BarcodeCaptureActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(personalID)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fname =
                    snapshot.child("first name").value.toString().trim().toUpperCase(Locale.ROOT)
                lname = snapshot.child("last name").value.toString().trim().toUpperCase(Locale.ROOT)
                name = "$fname $lname"

                findViewById<TextView>(R.id.textViewName).text = name

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}