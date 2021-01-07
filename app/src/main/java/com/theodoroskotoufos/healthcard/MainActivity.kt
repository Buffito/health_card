package com.theodoroskotoufos.healthcard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.loginButton).setOnClickListener {
            /// send to login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.createProfileButton).setOnClickListener {
            /// send to create a profile activity
            val intent = Intent(this, CreateProfileActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.scanButton).setOnClickListener {
            /// send to qr scanner
        }
    }

}