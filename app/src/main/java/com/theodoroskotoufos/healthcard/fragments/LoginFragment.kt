package com.theodoroskotoufos.healthcard.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.theodoroskotoufos.healthcard.R
import java.util.concurrent.Executor

class LoginFragment : Fragment() {
    private var exists: Boolean = false
    private var personalID: String = ""
    private var cardID: String = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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

        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")

        if (sharedPref.getBoolean("remember", false))
            login()

        initTexts(sharedPref, view)

        view.findViewById<Button>(R.id.singInButton).setOnClickListener {
            it.hideKeyboard()
            if (sharedPref.getBoolean("fill", false))
                login()
            else
                credentialCheck(sharedPref, databaseRef, it)
        }

    }

    private fun credentialCheck(sharedPref: SharedPreferences, databaseRef: DatabaseReference, view: View) {
        databaseChildExists(databaseRef)
        Handler(Looper.getMainLooper()).postDelayed({

            if (exists || checkSharedPreferences(sharedPref)) {
                login()
            } else {
                showSnackbar(view)
            }
        }, 200)

    }

    private fun showSnackbar(view: View){
        val mySnackbar =
            Snackbar.make(view, getString(R.string.login_fail), Snackbar.LENGTH_LONG)
        mySnackbar.view.setBackgroundColor(
            ContextCompat.getColor(
                requireActivity().application,
                R.color.green
            )
        )
        mySnackbar.show()
    }

    private fun login() {
        Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
            R.id.action_loginFragment_to_myProfileFragment
        )
    }

    private fun checkSharedPreferences(sharedPref: SharedPreferences): Boolean {
        return sharedPref.contains(personalID) && sharedPref.contains(cardID)
    }

    private fun initTexts(sharedPref: SharedPreferences, view: View) {
        val editTextArray: Array<EditText> = arrayOf(
            view.findViewById(R.id.personalID),
            view.findViewById(R.id.cardID)
        )
        val notEmpty = BooleanArray(2)
        // add text changed listeners to the editTexts

        val editor = sharedPref.edit()

        if (sharedPref.getBoolean("fill", false)) {
            view.findViewById<EditText>(R.id.personalID)
                .setText(sharedPref.getString("personalID", ""))
            view.findViewById<EditText>(R.id.cardID).setText(sharedPref.getString("cardID", ""))
            notEmpty[0] = true
            notEmpty[1] = true
            view.findViewById<Button>(R.id.singInButton).isEnabled =
                emptyArrayCheck(notEmpty)
        }

        for (i in 0..1) {
            editTextArray[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    view.findViewById<Button>(R.id.singInButton).isEnabled =
                        emptyArrayCheck(notEmpty)
                    personalID = view.findViewById<EditText>(R.id.personalID).text.toString().trim()
                    cardID = view.findViewById<EditText>(R.id.cardID).text.toString().trim()
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
                    notEmpty[i] = !s.isNullOrEmpty()
                }
            })
        }
    }

    private fun emptyArrayCheck(array: BooleanArray): Boolean {
        return array[0] && array[1]
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun databaseChildExists(databaseRef: DatabaseReference) {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(personalID) &&
                    snapshot.child(personalID).child("card id").value.toString() == cardID
                ) {
                    exists = true
                }

            }

            override fun onCancelled(error: DatabaseError) {
                exists = false

            }
        })
    }


}