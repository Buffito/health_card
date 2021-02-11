package com.theodoroskotoufos.healthcard.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.integration.android.IntentIntegrator
import com.theodoroskotoufos.healthcard.CaptureActivity
import com.theodoroskotoufos.healthcard.R
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.IOException

class MyProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
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

        val personalID = sharedPref.getString("personalID", "").toString().trim()

        Handler().postDelayed({
            initButtons(view)
            initName(view, personalID)
            initPhoto(view, personalID)
        }, 300)

    }

    private fun initButtons(view: View) {
        view.findViewById<Button>(R.id.infoButton).setOnClickListener {
            /// send to more info
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                R.id.action_myProfileFragment_to_profileInfoFragment
            )
        }

        view.findViewById<Button>(R.id.viewQrButton).setOnClickListener {
            /// send to view qr
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                R.id.action_myProfileFragment_to_viewQrFragment
            )
        }

        view.findViewById<Button>(R.id.scanQrButton).setOnClickListener {
            /// send to qr scanner
            scanQRCode()
        }
    }

    private fun initName(view: View, child: String) {
        var fname: String
        var lname: String
        var name: String
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(child)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fname =
                    snapshot.child("first name").value.toString().trim()
                lname = snapshot.child("last name").value.toString().trim()
                name = "$fname $lname"
                view.findViewById<TextView>(R.id.textViewName).text = name

            }

            override fun onCancelled(error: DatabaseError) {
                name = "Null"
                view.findViewById<TextView>(R.id.textViewName).text = name
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
                view.findViewById<CircleImageView>(R.id.profile_image).setImageBitmap(selfieBitmap)
            }.addOnFailureListener { }
    }

    private fun scanQRCode() {
        val integrator = IntentIntegrator(requireActivity()).apply {
            captureActivity = CaptureActivity::class.java
            setOrientationLocked(false)
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            setPrompt("Please wait")
        }
        integrator.initiateScan()
    }

}