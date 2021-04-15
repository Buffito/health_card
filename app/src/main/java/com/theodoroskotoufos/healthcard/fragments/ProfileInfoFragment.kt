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
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.iot.cbor.CborMap
import com.theodoroskotoufos.healthcard.R
import com.theodoroskotoufos.healthcard.User
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
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

        val sharedPreferences = initSharedPreferences()

        Handler(Looper.getMainLooper()).postDelayed({
            getFromCBOR(view, sharedPreferences)
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
                    view.findViewById<CircleImageView>(R.id.profile_image2)
                        .setImageBitmap(selfieBitmap)
                }.addOnFailureListener {
                    val bm = BitmapFactory.decodeResource(resources, R.drawable.white)
                    view.findViewById<CircleImageView>(R.id.profile_image2).setImageBitmap(bm)
                }
        } else {
            val bm = BitmapFactory.decodeResource(resources, R.drawable.white)
            view.findViewById<CircleImageView>(R.id.profile_image2).setImageBitmap(bm)
        }
    }

    private fun initCBORTexts(view: View, cborMap: CborMap) {
        view.findViewById<TextView>(R.id.textViewPersonFirstName).text =
            cborMap.get("fname").toString().replace(
                "\"",
                ""
            )
        view.findViewById<TextView>(R.id.textViewPersonLastName).text =
            cborMap.get("lname").toString().replace(
                "\"",
                ""
            )
        view.findViewById<TextView>(R.id.textViewDate).text = cborMap.get("dob").toString().replace(
            "\"",
            ""
        )
        view.findViewById<TextView>(R.id.textViewGender).text =
            cborMap.get("gender").toString().replace(
                "\"",
                ""
            )
        view.findViewById<TextView>(R.id.textViewCountry).text =
            cborMap.get("country").toString().replace(
                "\"",
                ""
            )
        view.findViewById<TextView>(R.id.textViewPersonalID).text =
            cborMap.get("pid").toString().replace(
                "\"",
                ""
            )
        view.findViewById<TextView>(R.id.textViewCardID).text =
            cborMap.get("cid").toString().replace(
                "\"",
                ""
            )
        view.findViewById<TextView>(R.id.textViewVaccineName).text =
            cborMap.get("vname").toString().replace(
                "\"",
                ""
            )
        view.findViewById<TextView>(R.id.textViewDateVaccine).text =
            cborMap.get("dov").toString().replace(
                "\"",
                ""
            )


    }

    private fun getFromCBOR(view: View, sharedPreferences: SharedPreferences) {
        val tempString = sharedPreferences.getString("cbor", "")
        val jsonObject = JSONObject(tempString)
        val user = getUser(jsonObject.getJSONObject("user"))
        val jsonString = Gson().toJson(user)
        val data = JSONObject(jsonString)
        val cborMap = CborMap.createFromJSONObject(data)

        initCBORTexts(view, cborMap)
    }

    private fun getUser(jsonObject: JSONObject): User {
        return User(
            jsonObject.getString("fname"),
            jsonObject.getString("lname"),
            jsonObject.getString("gender"),
            jsonObject.getString("dob"),
            jsonObject.getString("country"),
            jsonObject.getString("pid"),
            jsonObject.getString("cid"),
            jsonObject.getString("vname"),
            jsonObject.getString("dov")
        )
    }

}