package com.theodoroskotoufos.healthcard

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class CreateProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<Toolbar>(R.id.toolbar).title = title

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        val spinner: Spinner = findViewById(R.id.spinner)
        val applyButton = findViewById<Button>(R.id.applyButton)

        ArrayAdapter.createFromResource(
                this,
                R.array.gender_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
            spinner.prompt = "Gender"
        }


        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            ///start selfie activity
        }

        applyButton.setOnClickListener {
            /// save to firebase/shared preferences and open user profile
            with (sharedPref.edit()) {
                putString("vaccine_name", findViewById<Button>(R.id.editTextVaccineName).text as String?)
                putString("vaccine_date", findViewById<Button>(R.id.editTextDateVaccine).text as String?)
                apply()
            }

            val intent = Intent(this, UserProfile::class.java)
            startActivity(intent)
        }
    }

    private fun isTextValid(view: EditText): Boolean{
        /// check if view is valid
        if (view.text.isBlank() || view.text.isEmpty())
            return false
        return true
    }

    @Throws(ParseException::class)
    private fun isDateFormatValid(date: String): Boolean{
        /// check if date is a valid one dd/mm/yyyy
        val formatter = SimpleDateFormat("dd/mm/yyyy")
        return formatter.parse(date) != null
    }
}