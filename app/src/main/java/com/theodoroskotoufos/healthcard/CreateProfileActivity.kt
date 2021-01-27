package com.theodoroskotoufos.healthcard

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.FirebaseDatabase


class CreateProfileActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var gender: String = ""
    private var empty = BooleanArray(8)
    private var personalID: String = ""

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        setSupportActionBar(findViewById(R.id.toolbar))
        findViewById<Toolbar>(R.id.toolbar).title = title

        initTexts()
        initSpinner()

        findViewById<Button>(R.id.nextButton).setOnClickListener {
            /// save to firebase/shared preferences and open user profile

            if (emptyArrayCheck()) {

                saveToFirebase()
                saveToSharedPref()

                val intent = Intent(this, CameraActivity::class.java)
                 intent.putExtra("personalID", personalID)
                 intent.putExtra("gender", gender)
                 intent.putExtra(
                     "country",
                     findViewById<EditText>(R.id.editTextCountry).text.toString()
                 )
                // intent.putExtra("camera", "selfie")

                startActivity(intent)
            }
        }
    }

    private fun emptyArrayCheck(): Boolean {
        return empty[0] && empty[1] && empty[2] && empty[3] && empty[4] && empty[5] && empty[6] && empty[7]
    }

    private fun isDateFormatValid(date: EditText): Boolean {
        /// check if date is a valid one dd/mm/yyyy
        return false
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        gender = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        gender = "Male"
    }

    private fun initTexts() {
        val editTextList = arrayOf(
            findViewById<EditText>(R.id.editTextPersonFirstName),
            findViewById<EditText>(R.id.editTextPersonLastName),
            findViewById<EditText>(R.id.editTextDate),
            findViewById<EditText>(R.id.editTextCountry),
            findViewById<EditText>(R.id.editTextPersonalID),
            findViewById<EditText>(R.id.editTextCardID),
            findViewById<EditText>(R.id.editTextVaccineName),
            findViewById<EditText>(R.id.editTextDateVaccine)
        )

        for (i in 0..7) {
            editTextList[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    findViewById<Button>(R.id.nextButton).isEnabled = emptyArrayCheck()
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

    private fun initSpinner() {
        val spinner: Spinner = findViewById(R.id.spinner)
        val genderArray = arrayOf<String?>("Male", "Female")
        val arrayAdapter: ArrayAdapter<Any?> =
            ArrayAdapter<Any?>(this, R.layout.spinner_list, genderArray)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_list)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = this
    }

    private fun saveToFirebase() {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")
        personalID = findViewById<EditText>(R.id.editTextPersonalID).text.toString()
        val myRef = databaseRef.child(personalID)

        myRef.child("first name")
            .setValue(findViewById<EditText>(R.id.editTextPersonFirstName).text.toString())
        myRef.child("last name")
            .setValue(findViewById<EditText>(R.id.editTextPersonLastName).text.toString())
        myRef.child("gender")
            .setValue(gender)
        myRef.child("date of birth")
            .setValue(findViewById<EditText>(R.id.editTextDate).text.toString())
        myRef.child("country")
            .setValue(findViewById<EditText>(R.id.editTextCountry).text.toString())
        myRef.child("personal id")
            .setValue(personalID)
        myRef.child("card id")
            .setValue(findViewById<EditText>(R.id.editTextCardID).text.toString())
    }

    private fun saveToSharedPref() {
        val sharedPref = this@CreateProfileActivity.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(
            "vaccine_name",
            findViewById<EditText>(R.id.editTextVaccineName).text.toString()
        )
        editor.putString(
            "vaccine_date",
            findViewById<EditText>(R.id.editTextDateVaccine).text.toString()
        )
        editor.putString("personalID", personalID)
        editor.putString("cardID", findViewById<EditText>(R.id.editTextCardID).text.toString())
        editor.apply()
    }
}