package com.theodoroskotoufos.healthcard.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.database.FirebaseDatabase
import com.theodoroskotoufos.healthcard.R
import kotlinx.android.synthetic.main.fragment_create_profile.*
import java.util.*

class CreateProfileFragment : Fragment() {
    private var gender: String = ""
    private var personalID: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_create_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.title = getString(R.string.create_profile)

        initTexts(view)
        initSpinner(view)

        view.findViewById<Button>(R.id.nextButton).setOnClickListener {
            /// save to firebase/shared preferences and open user profile
            saveToFirebase(view)
            saveToSharedPref(view)


            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                R.id.action_createProfileFragment_to_filler
            )
        }
    }

    private fun emptyArrayCheck(array: BooleanArray): Boolean {
        return array[0] && array[1] && array[2] && array[3] && array[4] && array[5] && array[6] && array[7]
    }


    private fun initTexts(view: View) {
        val editTextArray: Array<EditText> = arrayOf(
            view.findViewById(R.id.editTextPersonFirstName),
            view.findViewById(R.id.editTextPersonLastName),
            view.findViewById(R.id.editTextDate),
            view.findViewById(R.id.editTextCountry),
            view.findViewById(R.id.editTextPersonalID),
            view.findViewById(R.id.editTextCardID),
            view.findViewById(R.id.editTextVaccineName),
            view.findViewById(R.id.editTextDateVaccine)
        )

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
                    if (i == 2 || i == 7) {
                        dateCheck(s, i, editTextArray, notEmpty)
                        return
                    }

                    notEmpty[i] = !s.isNullOrEmpty()

                }
            })
        }
    }

    private fun dateCheck(
        s: CharSequence?,
        i: Int,
        editTextArray: Array<EditText>,
        notEmpty: BooleanArray
    ) {
        if (s != null) {
            if (s.isNotEmpty()) {
                val regex =
                    "^(?:(?:31([/\\-.])(?:0?[13578]|1[02]))\\1|(?:(?:29|30)([/\\-.])(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$|^(?:29([/\\-.])0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))\$|^(?:0?[1-9]|1\\d|2[0-8])([/\\-.])(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$".toRegex()
                if (!s.toString().contains(regex)) {
                    editTextArray[i].error =
                        getString(R.string.invalid_date)

                    notEmpty[i] = false
                } else {
                    if (s.length == 10) {
                        val ss =
                            s[6].toString() + s[7].toString() + s[8].toString() + s[9].toString()
                        if (ss.toInt() > Calendar.getInstance()
                                .get(Calendar.YEAR)
                        ) {
                            editTextArray[i].error =
                                getString(R.string.future)

                            notEmpty[i] = false
                        } else

                            notEmpty[i] = true
                    }


                }

            }
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

    private fun saveToFirebase(view: View) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")
        personalID = view.findViewById<EditText>(R.id.editTextPersonalID).text.toString()
        val myRef = databaseRef.child(personalID)

        myRef.child("first name")
            .setValue(view.findViewById<EditText>(R.id.editTextPersonFirstName).text.toString())
        myRef.child("last name")
            .setValue(view.findViewById<EditText>(R.id.editTextPersonLastName).text.toString())
        myRef.child("gender")
            .setValue(gender)
        myRef.child("date of birth")
            .setValue(view.findViewById<EditText>(R.id.editTextDate).text.toString())
        myRef.child("country")
            .setValue(view.findViewById<EditText>(R.id.editTextCountry).text.toString())
        myRef.child("personal id")
            .setValue(personalID)
        myRef.child("card id")
            .setValue(view.findViewById<EditText>(R.id.editTextCardID).text.toString())
    }

    private fun saveToSharedPref(view: View) {

        val mainKey = MasterKey.Builder(requireContext())
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
            requireActivity(),
            "sharedPrefsFile",
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val editor = sharedPref.edit()
        editor.putString(
            "vaccine_name",
            view.findViewById<EditText>(R.id.editTextVaccineName).text.toString()
        )
        editor.putString(
            "vaccine_date",
            view.findViewById<EditText>(R.id.editTextDateVaccine).text.toString()
        )
        editor.putString("personalID", personalID)
        editor.putString("cardID", view.findViewById<EditText>(R.id.editTextCardID).text.toString())
        editor.putString("gender", gender)
        editor.putString(
            "country",
            view.findViewById<EditText>(R.id.editTextCountry).text.toString()
        )
        editor.apply()

    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}