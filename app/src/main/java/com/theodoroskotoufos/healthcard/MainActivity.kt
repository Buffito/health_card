package com.theodoroskotoufos.healthcard

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private val PERMISSIONS_REQUEST_CODE = 10
    private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!hasPermissions(applicationContext)) {
            // Request camera-related permissions
            requestPermissions(PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        }

        init()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                Toast.makeText(applicationContext, "Permission request granted", Toast.LENGTH_LONG)
                    .show()

            } else {
                Toast.makeText(applicationContext, "Permission request denied", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun init() {
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
            val intent = Intent(applicationContext, BarcodeCaptureActivity::class.java)
            startActivity(intent)

        }
    }
}
