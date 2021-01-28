package com.theodoroskotoufos.healthcard

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class CreateProfileActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var gender: String = ""
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

    private fun emptyArrayCheck(array: BooleanArray): Boolean {
        return array[0] && array[1] && array[2] && array[3] && array[4] && array[5] && array[6] && array[7]
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

        val empty = BooleanArray(8)

        for (i in 0..7) {
            editTextList[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (emptyArrayCheck(empty)) {
                        findViewById<Button>(R.id.nextButton).isEnabled = emptyArrayCheck(empty)
                        findViewById<EditText>(R.id.editTextDateVaccine).hideKeyboard()
                    }

                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (i == 2 || i == 7) {

                        if (s != null) {
                            var day = false
                            var month = false
                            var year = false

                            if (s.length in 5..8) {
                                val ss = s[0].toString().trim() + s[1].toString().trim()
                                if (ss.toInt() > 31) {
                                    editTextList[i].error =
                                        "Can't have more than 31 days in a month."
                                } else
                                    day = true

                            } else if (s.length in 10..13) {
                                val ss = s[5].toString().trim() + s[6].toString().trim()
                                if (ss.toInt() > 12) {
                                    editTextList[i].error =
                                        "Can't have more than 12 months in a year."
                                } else
                                    month = true
                            } else if (s.length == 14) {
                                val ss = s[10].toString().trim() + s[11].toString()
                                    .trim() + s[12].toString().trim() + s[13].toString().trim()
                                if (ss.toInt() > Calendar.getInstance().get(Calendar.YEAR))
                                    editTextList[i].error =
                                        "Are you from the future?"
                                if (i == 7) {
                                    if (ss.toInt() > Calendar.getInstance().get(Calendar.YEAR) ||
                                        (ss.toInt() - Calendar.getInstance().get(Calendar.YEAR) > 1)
                                    )
                                        editTextList[i].error =
                                            "Vaccines for Covid were not out that year."
                                } else
                                    year = true

                            }

                            if ((!day && !month && !year) && (s.length == 14)) {
                                editTextList[i].error = "Invalid date format."
                                empty[i] = true
                            } else
                                empty[i] = false

                        }
                    } else
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

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}