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
import java.text.ParseException


class CreateProfile : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var gender : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<Toolbar>(R.id.toolbar).title = title


        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val spinner: Spinner = findViewById(R.id.spinner)
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")


        ArrayAdapter.createFromResource(
                this,
                R.array.gender_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
            spinner.onItemSelectedListener = this
        }


        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            ///start selfie activity
        }


        findViewById<Button>(R.id.applyButton).setOnClickListener {
            /// save to firebase/shared preferences and open user profile
            if (areFieldsValid()){
                val userID = findViewById<EditText>(R.id.editTextPersonalID).text.toString()
                val myRef = databaseRef.child(userID)
                myRef.child("first name").setValue(findViewById<EditText>(R.id.editTextPersonFirstName).text.toString())
                myRef.child("last name").setValue(findViewById<EditText>(R.id.editTextPersonLastName).text.toString())
                myRef.child("date of birth").setValue(findViewById<EditText>(R.id.editTextDate).text.toString())
                myRef.child("gender").setValue(gender)
                myRef.child("iso code").setValue(findViewById<EditText>(R.id.editTextCountryISOCode).text.toString())
                myRef.child("user id").setValue(userID)
                myRef.child("card id").setValue(findViewById<EditText>(R.id.editTextCardID).text.toString())
                with (sharedPref.edit()) {
                    putString("vaccine_name", findViewById<EditText>(R.id.editTextVaccineName).text.toString())
                    putString("vaccine_date", findViewById<EditText>(R.id.editTextDateVaccine).text.toString())
                    apply()
                }
            }
            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)

        }

    }

    private fun areFieldsValid(): Boolean {
        val empty = arrayListOf<String>()
        if (isTextEmpty(findViewById(R.id.editTextPersonFirstName))){
            empty.add("Fist name")
        }
        if (isTextEmpty(findViewById(R.id.editTextPersonFirstName))){
            empty.add("Last name")
        }
        if (isTextEmpty(findViewById(R.id.editTextPersonFirstName))){
            empty.add("Date of birth")
        }
        if (isTextEmpty(findViewById(R.id.editTextPersonFirstName))){
            empty.add("ISO code")
        }
        if (isTextEmpty(findViewById(R.id.editTextPersonFirstName))){
            empty.add("User id")
        }
        if (isTextEmpty(findViewById(R.id.editTextPersonFirstName))){
            empty.add("Card id")
        }
        if (isTextEmpty(findViewById(R.id.editTextVaccineName))){
            empty.add("Vaccine name")
        }
        if (isTextEmpty(findViewById(R.id.editTextDateVaccine))){
            empty.add("Vaccination date")
        }
        return if (empty.isNotEmpty()){
            if (empty.size > 1)
                Toast.makeText(this, "Can't have empty fields :)", LENGTH_SHORT).show()
            else
                Toast.makeText(this, empty.last() + " field is empty :)", LENGTH_SHORT).show()
            false
        }else
            true

    }

    private fun isTextEmpty(view: EditText): Boolean {
        /// check if view is valid
        if (view.text.isBlank() || view.text.isEmpty()){
            return true
        }
        return false
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