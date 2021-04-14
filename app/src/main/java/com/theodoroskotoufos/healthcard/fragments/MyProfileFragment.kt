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
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
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

        val sharedPreferences = initSharedPreferences()
        Handler(Looper.getMainLooper()).postDelayed({
            initButtons(view)
            initName(view, sharedPreferences)
            initPhoto(view, sharedPreferences)
        }, 300)

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

    private fun initButtons(view: View) {
        view.findViewById<Button>(R.id.infoButton).setOnClickListener {
            /// send to more info
            Navigation.findNavController(requireActivity(), R.id.fragment_container2).navigate(
                R.id.action_myProfileFragment_to_profileInfoFragment
            )
        }

        view.findViewById<Button>(R.id.viewQrButton).setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragment_container2).navigate(
                R.id.action_myProfileFragment_to_viewQrFragment
            )
        }

        view.findViewById<Button>(R.id.scanQrButton).setOnClickListener {
            /// send to qr scanner
            scanQRCode()
        }

        view.findViewById<Button>(R.id.settingsButton).setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragment_container2).navigate(
                R.id.action_myProfileFragment_to_settingsFragment
            )
        }
    }

    private fun initName(view: View, sharedPreferences: SharedPreferences) {
        val fname: String = sharedPreferences.getString("first_name", "").toString()
        val lname: String = sharedPreferences.getString("last_name", "").toString()
        val name = "$fname $lname"
        view.findViewById<TextView>(R.id.textViewName).text = name
    }

    private fun initPhoto(view: View, sharedPreferences: SharedPreferences) {
        val child = sharedPreferences.getString("personalID", "").toString()
        if (child.isNotEmpty()) {
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
                    view.findViewById<CircleImageView>(R.id.profile_image)
                        .setImageBitmap(selfieBitmap)
                }.addOnFailureListener {
                    val bm = BitmapFactory.decodeResource(resources, R.drawable.white)
                    view.findViewById<CircleImageView>(R.id.profile_image).setImageBitmap(bm)
                }
        } else {
            val bm = BitmapFactory.decodeResource(resources, R.drawable.white)
            view.findViewById<CircleImageView>(R.id.profile_image).setImageBitmap(bm)
        }
    }

    private fun scanQRCode() {
        val integrator = IntentIntegrator(requireActivity()).apply {
            captureActivity = CaptureActivity::class.java
            setOrientationLocked(false)
            setBeepEnabled(true)
            setCameraId(0)
            setBarcodeImageEnabled(true)
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            setPrompt(getString(R.string.scan_prompt))
        }
        integrator.initiateScan()
    }

}