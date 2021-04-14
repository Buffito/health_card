package com.theodoroskotoufos.healthcard.fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.database.FirebaseDatabase
import com.theodoroskotoufos.healthcard.R
import com.theodoroskotoufos.healthcard.User
import com.theodoroskotoufos.healthcard.facetec.facetecapp.FacetecAppActivity
import kotlinx.android.synthetic.main.fragment_create_profile.*
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.Writer
import java.util.*

class CreateProfileFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    private var gender: String = ""
    private var personalID: String = ""

    private lateinit var date: EditText
    private lateinit var vdate: EditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_create_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = getString(R.string.create_profile)
        date = view.findViewById(R.id.editTextDate)
        vdate = view.findViewById(R.id.editTextDateVaccine)

        val sharedPreferences = initSharedPreferences()
        initTexts(view)
        initSpinner(view)


        view.findViewById<Button>(R.id.nextButton).setOnClickListener {
            /// save to firebase/shared preferences and open user profile
            saveToSharedPref(view, sharedPreferences)
            toJson(view)


            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(requireContext(), FacetecAppActivity::class.java)
                startActivity(intent)
            }, 300)
        }

    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            this,
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.updateDate(2000, 0, 0)
        datePickerDialog.show()
    }

    private fun showVacDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            this,
            Calendar.getInstance()[Calendar.YEAR],
            Calendar.getInstance()[Calendar.MONTH],
            Calendar.getInstance()[Calendar.DAY_OF_MONTH]
        )
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        var myMonth = month
        myMonth++
        val temp = "$dayOfMonth/$myMonth/$year"
        if (year < (Calendar.getInstance().get(Calendar.YEAR) - 1)) {
            date.setText(temp)
        } else {
            vdate.setText(temp)
        }
    }

    private fun emptyArrayCheck(array: BooleanArray): Boolean {
        return array[0] && array[1] && array[2] && array[3] && array[4] && array[5] && array[6] && array[7]
    }

    private fun initTexts(view: View) {
        view.findViewById<TextView>(R.id.editTextDate).setOnClickListener {
            showDatePickerDialog()
            it.hideKeyboard()
        }

        view.findViewById<TextView>(R.id.editTextDateVaccine).setOnClickListener {
            showVacDatePickerDialog()
            it.hideKeyboard()
        }


        val editTextArray: Array<EditText> = arrayOf(
            view.findViewById(R.id.editTextPersonFirstName),
            view.findViewById(R.id.editTextPersonLastName),
            date,
            view.findViewById(R.id.editTextCountry),
            view.findViewById(R.id.editTextPersonalID),
            view.findViewById(R.id.editTextCardID),
            view.findViewById(R.id.editTextVaccineName),
            vdate
        )

        if (date.hasFocus() || vdate.hasFocus())
            view.hideKeyboard()

        val notEmpty = BooleanArray(8)

        for (i in 0..7) {
            editTextArray[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (emptyArrayCheck(notEmpty)) {
                        view.findViewById<Button>(R.id.nextButton).isEnabled =
                            emptyArrayCheck(notEmpty)
                        editTextArray[i].hideKeyboard()
                        view.findViewById<Button>(R.id.nextButton).requestFocus()
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
                    notEmpty[i] = !s.isNullOrEmpty()

                }
            })
        }
    }

    private fun initSpinner(view: View) {
        val spinner: Spinner = view.findViewById(R.id.spinner)
        val genderArray = arrayOf<String?>(getString(R.string.male), getString(R.string.female))
        val arrayAdapter: ArrayAdapter<Any?> =
            ArrayAdapter<Any?>(requireActivity(), R.layout.spinner_list, genderArray)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_list)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                gender = getString(R.string.male)
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                gender = parent?.getItemAtPosition(position).toString()
            }

        }
    }

    private fun saveToFirebase(view: View, jsonString: String) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")
        personalID = view.findViewById<EditText>(R.id.editTextPersonalID).text.toString()
        val myRef = databaseRef.child(personalID)

        myRef.child("cbor")
            .setValue(jsonString)

    }

    private fun initSharedPreferences(): SharedPreferences {
        val mainKey = MasterKey.Builder(requireContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            requireActivity(),
            "sharedPrefsFile",
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private fun saveToSharedPref(view: View, sharedPreferences: SharedPreferences) {
        val editor = sharedPreferences.edit()
        editor.putString(
            "first_name",
            view.findViewById<EditText>(R.id.editTextPersonFirstName).text.toString()
        )
        editor.putString(
            "last_name",
            view.findViewById<EditText>(R.id.editTextPersonLastName).text.toString()
        )
        editor.putString("personalID", personalID)
        editor.putString("cardID", view.findViewById<EditText>(R.id.editTextCardID).text.toString())
        editor.apply()

    }

    private fun toJson(view: View) {
        val json = JSONObject()

        val user = User(
            view.findViewById<EditText>(R.id.editTextPersonFirstName).text.toString(),
            view.findViewById<EditText>(R.id.editTextPersonLastName).text.toString(),
            gender,
            view.findViewById<TextView>(R.id.editTextDate).text.toString(),
            view.findViewById<EditText>(R.id.editTextCountry).text.toString(),
            view.findViewById<EditText>(R.id.editTextPersonalID).text.toString(),
            view.findViewById<EditText>(R.id.editTextCardID).text.toString(),
            view.findViewById<EditText>(R.id.editTextVaccineName).text.toString(),
            view.findViewById<TextView>(R.id.editTextDateVaccine).text.toString()
        )

        json.put("user", addUser(user))
        saveToFirebase(view, json.toString())
        saveJson(json.toString())

    }

    private fun addUser(user: User): JSONObject {
        return JSONObject()
            .put("fname", user.fname)
            .put("lname", user.lname)
            .put("gender", user.gender)
            .put("dob", user.dob)
            .put("country", user.country)
            .put("pid", user.pid)
            .put("cid", user.cid)
            .put("vname", user.vname)
            .put("dov", user.dov)

    }

    private fun saveJson(jsonString: String) {
        val output: Writer
        val fileName = "user.json"
        val file = File(context?.filesDir?.absolutePath, fileName)
        file.createNewFile()
        output = BufferedWriter(FileWriter(file))
        output.write(jsonString)
        output.close()
    }


    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}