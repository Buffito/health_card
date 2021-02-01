package com.theodoroskotoufos.healthcard.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.theodoroskotoufos.healthcard.MyProfileActivity
import com.theodoroskotoufos.healthcard.R

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

        val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users")

        view.findViewById<CheckBox>(R.id.checkBox).isChecked = sharedPref.getBoolean("remember", false)

        initTexts(editor,view)

        if (view.findViewById<CheckBox>(R.id.checkBox).isChecked) {
            login(sharedPref)
        }

        view.findViewById<Button>(R.id.singInButton).setOnClickListener {
            it.hideKeyboard()
            databaseChildExists(databaseRef)
            Handler().postDelayed({
                if (exists) {
                    login(sharedPref)
                } else {
                    val mySnackbar =
                        Snackbar.make(it, "PersonalId or CardId was invalid.", Snackbar.LENGTH_LONG)
                    mySnackbar.view.setBackgroundColor(ContextCompat.getColor(requireActivity().application, R.color.green))
                    mySnackbar.show()
                }
            }, 300)

        }

        // checkBox on/off mechanic

        view.findViewById<CheckBox>(R.id.checkBox).setOnClickListener {
            if (view.findViewById<CheckBox>(R.id.checkBox).isChecked) {
                editor.putBoolean("remember", true)
            } else {
                editor.putBoolean("remember", false)
            }
            editor.apply()
        }
    }

    private fun login(sharedPref: SharedPreferences){
        val intent = Intent(activity, MyProfileActivity::class.java)
        intent.putExtra("personalID", sharedPref.getString("personalID", ""))
        startActivity(intent)
    }

    private fun initTexts(editor: SharedPreferences.Editor, view: View) {
        val editTextList = arrayOf(
            view.findViewById<EditText>(R.id.personalID),
            view.findViewById<EditText>(R.id.cardID)
        )
        val notEmpty = BooleanArray(2)
        // add text changed listeners to the editTexts

        for (i in 0..1) {
            editTextList[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    view.findViewById<Button>(R.id.singInButton).isEnabled = emptyArrayCheck(notEmpty)
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

}