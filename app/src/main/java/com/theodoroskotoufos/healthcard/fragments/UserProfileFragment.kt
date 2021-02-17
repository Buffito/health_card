package com.theodoroskotoufos.healthcard.fragments

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.theodoroskotoufos.healthcard.R
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.IOException

class UserProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
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

        val userID = sharedPref.getString("userID", "").toString()
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(userID)

        initPhoto(view, userID)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                view.findViewById<TextView>(R.id.textPersonFirstName).text =
                    snapshot.child("first name").value.toString()
                view.findViewById<TextView>(R.id.textPersonLastName).text =
                    snapshot.child("last name").value.toString()
                view.findViewById<TextView>(R.id.textDate).text =
                    snapshot.child("gender").value.toString()
                view.findViewById<TextView>(R.id.textGender).text =
                    snapshot.child("date of birth").value.toString()
                view.findViewById<TextView>(R.id.textCountry).text =
                    snapshot.child("country").value.toString()
                view.findViewById<TextView>(R.id.textPersonalID).text =
                    snapshot.child("personal id").value.toString()
                view.findViewById<TextView>(R.id.textCardID).text =
                    snapshot.child("card id").value.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
                parentFragmentManager.popBackStackImmediate()
            }
        })
    }

    private fun initPhoto(view: View, child: String) {
        val imagesRef = FirebaseStorage.getInstance().reference.child("images").child(child)
        val selfieRef = imagesRef.child("selfie")
        var selfieFile: File? = null
        try {
            selfieFile = File.createTempFile("selfie", ".jpeg")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var selfieBitmap: Bitmap
        val finalSelfieFile = selfieFile
        selfieRef.getFile(selfieFile!!)
            .addOnSuccessListener {
                // Local temp file has been created
                selfieBitmap = BitmapFactory.decodeFile(finalSelfieFile!!.path)
                view.findViewById<CircleImageView>(R.id.profile_image3).setImageBitmap(selfieBitmap)
            }.addOnFailureListener { }
    }


}