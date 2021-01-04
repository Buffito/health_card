package com.theodoroskotoufos.healthcard

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.google.firebase.database.FirebaseDatabase
import com.theodoroskotoufos.healthcard.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        findViewById<Button>(R.id.singInButton).isEnabled = false
        var personalTextChanged = false
        var cardTextChanged = false
        var personalID : String = ""
        var cardID : String = ""

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")

        findViewById<EditText>(R.id.personalID).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                findViewById<Button>(R.id.singInButton).isEnabled = personalTextChanged && cardTextChanged
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                personalTextChanged = s?.isNotEmpty() ?: false
                if (personalTextChanged)
                    personalID = s.toString().trim()
            }
        })

        findViewById<EditText>(R.id.cardID).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                findViewById<Button>(R.id.singInButton).isEnabled = personalTextChanged && cardTextChanged
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cardTextChanged = s?.isNotEmpty() ?: false
                if (cardTextChanged)
                    cardID = s.toString().trim()
            }
        })

        findViewById<Button>(R.id.singInButton).setOnClickListener{
            Toast.makeText(this,"click", LENGTH_SHORT).show()
            if (sharedPref.getString("userID",null).equals(personalID) &&
                    sharedPref.getString("cardID",null).equals(cardID))
                        login()
        }

    }

    private fun login(){
        Toast.makeText(this,"login", LENGTH_SHORT).show()
    }
}