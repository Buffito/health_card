package com.theodoroskotoufos.healthcard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private var personalID = ""
    private var cardID = ""
    private var exists = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // initializing variables to be used later

        val sharedPref = this@LoginActivity.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")

        findViewById<CheckBox>(R.id.checkBox).isChecked = sharedPref.getBoolean("remember", false)

        initTexts(editor)

        if (findViewById<CheckBox>(R.id.checkBox).isChecked) {
            login(sharedPref)
        }
        sharedPref.getString("personalID", "")?.let { Log.d("SHARED", it) }

        // sing in button click listener
        findViewById<Button>(R.id.singInButton).setOnClickListener {
            it.hideKeyboard()
            databaseChildExists(databaseRef)
            Handler().postDelayed({
                if (exists) {
                    login(sharedPref)
                } else {
                    val mySnackbar =
                        Snackbar.make(it, "PersonalId or CardId was invalid.", Snackbar.LENGTH_LONG)
                    mySnackbar.view.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                    mySnackbar.show()
                }
            }, 300)

        }

        // checkBox on/off mechanic

        findViewById<CheckBox>(R.id.checkBox).setOnClickListener {
            if (findViewById<CheckBox>(R.id.checkBox).isChecked) {
                editor.putBoolean("remember", true)
            } else {
                editor.putBoolean("remember", false)
            }
            editor.apply()
        }
    }

    private fun login(sharedPref: SharedPreferences){
        val intent = Intent(this, MyProfileActivity::class.java)
        intent.putExtra("personalID", sharedPref.getString("personalID", ""))
        startActivity(intent)
    }

    private fun emptyArrayCheck(empty: BooleanArray): Boolean {
        return empty[0] && empty[1]
    }

    private fun databaseChildExists(databaseRef: DatabaseReference) {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(personalID) &&
                    snapshot.child(personalID).child("card id").value.toString().equals(cardID)
                ) {
                    exists = true
                }

            }

            override fun onCancelled(error: DatabaseError) {
                exists = false

            }
        })
    }

    private fun initTexts(editor: SharedPreferences.Editor) {
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
                    editor.putString("personalID", personalID)
                    editor.apply()
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
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}

