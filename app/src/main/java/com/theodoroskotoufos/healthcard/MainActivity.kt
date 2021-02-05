package com.theodoroskotoufos.healthcard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.zxing.integration.android.IntentIntegrator
import com.theodoroskotoufos.healthcard.fragments.UserProfileFragment


class MainActivity : AppCompatActivity() {
    private lateinit var container: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        container = findViewById(R.id.fragment_container)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                val userlID = result.contents.trim()
                val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putString("userID", userlID)
                editor.apply()

                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                try {
                    supportFragmentManager.popBackStackImmediate(
                        UserProfileFragment().toString(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                } catch (e: IllegalStateException) {
                }

                transaction.addToBackStack(UserProfileFragment().toString())
                transaction.replace(R.id.fragment_container, UserProfileFragment())
                transaction.commitAllowingStateLoss()
                supportFragmentManager.executePendingTransactions()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}