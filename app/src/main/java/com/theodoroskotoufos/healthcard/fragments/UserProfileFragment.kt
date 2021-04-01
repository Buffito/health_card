package com.theodoroskotoufos.healthcard.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.iot.cbor.CborArray
import com.google.iot.cbor.CborMap
import com.theodoroskotoufos.healthcard.R
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.io.File
import java.io.IOException

class UserProfileFragment : Fragment() {
    var user: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        user = this.requireArguments().getString("user").toString()

        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = JSONObject(user)
        val cborMap = CborMap.createFromJSONObject(data)

        initTexts(view, cborMap)
    }

    private fun initTexts(view: View, cborMap: CborMap){
        view.findViewById<TextView>(R.id.textPersonFirstName).text = cborMap.get("fname").toString().replace("\"","")
        view.findViewById<TextView>(R.id.textPersonLastName).text = cborMap.get("lname").toString().replace("\"","")
        view.findViewById<TextView>(R.id.textDate).text = cborMap.get("gender").toString().replace("\"","")
        view.findViewById<TextView>(R.id.textGender).text = cborMap.get("dob").toString().replace("\"","")
        view.findViewById<TextView>(R.id.textCountry).text = cborMap.get("country").toString().replace("\"","")
        view.findViewById<TextView>(R.id.textPersonalID).text = cborMap.get("pid").toString().replace("\"","")
        view.findViewById<TextView>(R.id.textCardID).text = cborMap.get("cid").toString().replace("\"","")
        view.findViewById<TextView>(R.id.textVaccineName).text = cborMap.get("vname").toString().replace("\"","")
        view.findViewById<TextView>(R.id.textDateVaccine).text = cborMap.get("dov").toString().replace("\"","")


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

    private fun getFromFirebase(view: View){
        val databaseRef = FirebaseDatabase.getInstance().reference.child("users").child(user)

        initPhoto(view, user)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                view.findViewById<TextView>(R.id.textPersonFirstName).text =
                    snapshot.child("first name").value.toString()
                view.findViewById<TextView>(R.id.textPersonLastName).text =
                    snapshot.child("last name").value.toString()
                view.findViewById<TextView>(R.id.textGender).text =
                    snapshot.child("gender").value.toString()
                view.findViewById<TextView>(R.id.textDate).text =
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

}