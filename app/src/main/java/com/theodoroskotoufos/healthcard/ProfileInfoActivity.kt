package com.theodoroskotoufos.healthcard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class ProfileInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_info)
        setSupportActionBar(findViewById(R.id.toolbar4))
        findViewById<Toolbar>(R.id.toolbar4).title = title
    }
}