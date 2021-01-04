package com.theodoroskotoufos.healthcard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase


class CreateProfile : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var gender : String = ""
    private var empty = BooleanArray(8)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        setSupportActionBar(findViewById(R.id.toolbar2))
        findViewById<Toolbar>(R.id.toolbar2).title = title

        findViewById<Button>(R.id.applyButton).isEnabled = false
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")

        val spinner: Spinner = findViewById(R.id.spinner)
        val genderArray = arrayOf<String?>("Male", "Female")
        val arrayAdapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(this, R.layout.spinner_list, genderArray)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_list)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = this



        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            ///start selfie activity
        }


        findViewById<Button>(R.id.applyButton).setOnClickListener {
            /// save to firebase/shared preferences and open user profile
            val userID = findViewById<EditText>(R.id.editTextPersonalID).text.toString()
            val myRef = databaseRef.child(userID)

    /*        myRef.child("first name").setValue(findViewById<EditText>(R.id.editTextPersonFirstName).text.toString())
            myRef.child("last name").setValue(findViewById<EditText>(R.id.editTextPersonLastName).text.toString())
            myRef.child("date of birth").setValue(findViewById<EditText>(R.id.editTextDate).text.toString())
            myRef.child("gender").setValue(gender)
            myRef.child("iso code").setValue(findViewById<EditText>(R.id.editTextCountryISOCode).text.toString())
            myRef.child("user id").setValue(userID)
            myRef.child("card id").setValue(findViewById<EditText>(R.id.editTextCardID).text.toString())
            with (sharedPref.edit()) {
                putString("vaccine_name", findViewById<EditText>(R.id.editTextVaccineName).text.toString())
                putString("vaccine_date", findViewById<EditText>(R.id.editTextDateVaccine).text.toString())
                putString("userID", userID)
                putString("cardID", findViewById<EditText>(R.id.editTextCardID).text.toString())
                apply()
            }   */

            val intent = Intent(this, UserProfile::class.java)
        //    intent.putExtra("userID",userID)
            startActivity(intent)

        }

        findViewById<EditText>(R.id.editTextPersonFirstName).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                findViewById<Button>(R.id.applyButton).isEnabled = emptyArrayCheck()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                empty[0] = !s.isNullOrEmpty()

            }
        })
        findViewById<EditText>(R.id.editTextPersonLastName).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                findViewById<Button>(R.id.applyButton).isEnabled = emptyArrayCheck()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                empty[1] = !s.isNullOrEmpty()
            }
        })
        findViewById<EditText>(R.id.editTextDate).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                findViewById<Button>(R.id.applyButton).isEnabled = emptyArrayCheck()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                empty[2] = !s.isNullOrEmpty()
            }
        })
        findViewById<EditText>(R.id.editTextCountryISOCode).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                findViewById<Button>(R.id.applyButton).isEnabled = emptyArrayCheck()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                empty[3] = !s.isNullOrEmpty()
            }
        })
        findViewById<EditText>(R.id.editTextPersonalID).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                findViewById<Button>(R.id.applyButton).isEnabled = emptyArrayCheck()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                empty[4] = !s.isNullOrEmpty()
            }
        })
        findViewById<EditText>(R.id.editTextCardID).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                findViewById<Button>(R.id.applyButton).isEnabled = emptyArrayCheck()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                empty[5] = !s.isNullOrEmpty()
            }
        })
        findViewById<EditText>(R.id.editTextVaccineName).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                findViewById<Button>(R.id.applyButton).isEnabled = emptyArrayCheck()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                empty[6] = !s.isNullOrEmpty()
            }
        })
        findViewById<EditText>(R.id.editTextDateVaccine).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                findViewById<Button>(R.id.applyButton).isEnabled = emptyArrayCheck()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                empty[7] = !s.isNullOrEmpty()
            }
        })
    }

    private fun emptyArrayCheck(): Boolean {
        return empty[0] && empty[1] && empty[2] && empty[3] && empty[4] && empty[5] && empty[6] && empty[7]
    }

    private fun isDateFormatValid(date: EditText): Boolean{
        /// check if date is a valid one dd/mm/yyyy
        return false
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        gender = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        gender = "male"
    }
}