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
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.android.material.snackbar.Snackbar
import com.theodoroskotoufos.healthcard.R

class LoginFragment : Fragment() {
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

        val sharedPreferences = initSharedPreferences()

        if (sharedPreferences.getBoolean("bio", false))
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                R.id.action_loginFragment_to_fingerFragment
            )
        else {
            if (sharedPreferences.getBoolean("remember", false))
                login()


            initTexts(sharedPreferences, view)
            view.findViewById<Button>(R.id.singInButton).setOnClickListener {
                it.hideKeyboard()
                if (credentialCheck(sharedPreferences, view)) {
                    login()
                } else {
                    showSnackbar(view)
                }
            }
        }
    }

    private fun credentialCheck(sharedPreferences: SharedPreferences, view: View): Boolean {
        return view.findViewById<EditText>(R.id.personalID).text.toString() == sharedPreferences.getString(
            "personalID",
            ""
        ).toString() &&
                view.findViewById<EditText>(R.id.cardID).text.toString() == sharedPreferences.getString(
            "cardID",
            ""
        ).toString()
    }

    private fun showSnackbar(view: View) {
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

    private fun initTexts(sharedPreferences: SharedPreferences, view: View) {
        val editTextArray: Array<EditText> = arrayOf(
            view.findViewById(R.id.personalID),
            view.findViewById(R.id.cardID)
        )
        val notEmpty = BooleanArray(2)
        // add text changed listeners to the editTexts

        val editor = sharedPreferences.edit()

        if (sharedPreferences.getBoolean("fill", false)) {
            view.findViewById<EditText>(R.id.personalID)
                .setText(sharedPreferences.getString("personalID", ""))
            view.findViewById<EditText>(R.id.cardID)
                .setText(sharedPreferences.getString("cardID", ""))
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




}