package com.theodoroskotoufos.healthcard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*


class LoginActivity : AppCompatActivity() {
    var personalID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // initializing variables to be used later

        findViewById<Button>(R.id.singInButton).isEnabled = false
        var cardID = ""

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")

        findViewById<CheckBox>(R.id.checkBox).isChecked = sharedPref.getBoolean("remember", false)

        val editTextList = arrayOf(
            findViewById<EditText>(R.id.personalID),
            findViewById<EditText>(R.id.cardID)
        )
        val empty = BooleanArray(2)
        // add text changed listeners to the editTexts

        for (i in 0..1) {
            editTextList[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    findViewById<Button>(R.id.singInButton).isEnabled = emptyArrayCheck(empty)
                    personalID = findViewById<EditText>(R.id.personalID).text.toString().trim()
                    cardID = findViewById<EditText>(R.id.cardID).text.toString().trim()
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    empty[i] = !s.isNullOrEmpty()
                }
            })
        }

        // sing in button click listener

        findViewById<Button>(R.id.singInButton).setOnClickListener {
            if (sharedPref.getString("personalID", null).equals(personalID) &&
                sharedPref.getString("cardID", null).equals(cardID)
            )
                login()
            else if (databaseChildExists(databaseRef, personalID) &&
                databaseRef.child("card id").equals(cardID)
            ) {
                login()
            } else {
                val snackbar =
                    Snackbar.make(it, "PersonalId or CardId was invalid.", Snackbar.LENGTH_LONG)
                snackbar.view.setBackgroundColor(resources.getColor(R.color.green))
                snackbar.show()
            }
            login()
        }

        // checkBox on/off mechanic

        findViewById<CheckBox>(R.id.checkBox).setOnClickListener {
            checkBoxOnOff(sharedPref)
        }
    }

    private fun login() {
        val intent = Intent(this, MyProfileActivity::class.java)
        intent.putExtra("personalID", personalID)
        startActivity(intent)
    }

    private fun emptyArrayCheck(empty: BooleanArray): Boolean {
        return empty[0] && empty[1]
    }

    private fun checkBoxOnOff(sharedPref: SharedPreferences) {
        if (findViewById<CheckBox>(R.id.checkBox).isChecked) {
            with(sharedPref.edit()) {
                putBoolean("remember", true)
                apply()
            }
        } else {
            with(sharedPref.edit()) {
                putBoolean("remember", false)
                apply()
            }
        }
    }

    private fun databaseChildExists(databaseRef: DatabaseReference, personalID: String): Boolean {
        var exists = false
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(personalID))
                    exists = true
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        return exists
    }
}

