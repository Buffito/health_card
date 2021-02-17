package com.theodoroskotoufos.healthcard.fragments

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.theodoroskotoufos.healthcard.R
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.IOException

class ProfileInfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_info, container, false)
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
        val personalID = sharedPref.getString("personalID", "").toString()
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(personalID)


        Handler(Looper.getMainLooper()).postDelayed({
            initPhoto(view, personalID)
            initViews(databaseRef, sharedPref, view)
        }, 300)

    }

    private fun initViews(
        databaseRef: DatabaseReference,
        sharedPref: SharedPreferences,
        view: View
    ) {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                view.findViewById<TextView>(R.id.textViewPersonFirstName).text =
                    snapshot.child("first name").value.toString()
                view.findViewById<TextView>(R.id.textViewPersonLastName).text =
                    snapshot.child("last name").value.toString()
                view.findViewById<TextView>(R.id.textViewDate).text =
                    snapshot.child("gender").value.toString()
                view.findViewById<TextView>(R.id.textViewGender).text =
                    snapshot.child("date of birth").value.toString()
                view.findViewById<TextView>(R.id.textViewCountry).text =
                    snapshot.child("country").value.toString()
                view.findViewById<TextView>(R.id.textViewPersonalID).text =
                    snapshot.child("personal id").value.toString()
                view.findViewById<TextView>(R.id.textViewCardID).text =
                    snapshot.child("card id").value.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
            }
        })

        view.findViewById<TextView>(R.id.textViewVaccineName).text =
            sharedPref.getString("vaccine_name", "")
        view.findViewById<TextView>(R.id.textViewDateVaccine).text =
            sharedPref.getString("vaccine_date", "")
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
                view.findViewById<CircleImageView>(R.id.profile_image2).setImageBitmap(selfieBitmap)
            }.addOnFailureListener { }
    }

}