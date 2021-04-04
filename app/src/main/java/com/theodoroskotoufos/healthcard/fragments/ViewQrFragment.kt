package com.theodoroskotoufos.healthcard.fragments

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.iot.cbor.CborMap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.theodoroskotoufos.healthcard.R
import com.theodoroskotoufos.healthcard.User
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.nio.channels.FileChannel
import java.nio.charset.Charset


class ViewQrFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_qr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getFromCBOR(view)


    }

    private fun getFromCBOR(view: View) {
        val jsonObject = JSONObject(readFile())
        val user = getUser(jsonObject.getJSONObject("user"))
        val jsonString = Gson().toJson(user)
        val data = JSONObject(jsonString)
        val cborMap = CborMap.createFromJSONObject(data)
        generateQRCode(cborMap.toString(), view)
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

    private fun readFile(): String {
        val sharedPreferences = initSharedPreferences()
        val fileName = sharedPreferences.getString("personalID", "").toString() + ".json"
        val file = File(context?.filesDir!!.absolutePath, fileName)

        val stream = FileInputStream(file)

        var jsonString: String
        stream.use { stream ->
            val fileChannel = stream.channel
            val mappedByteBuffer = fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                0,
                fileChannel.size()
            )
            jsonString = Charset.defaultCharset().decode(mappedByteBuffer).toString()
        }
        return jsonString
    }

    private fun generateQRCode(text: String, view: View) {
        val width = 800
        val height = 800
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.TRANSPARENT)
                }
            }
        } catch (e: WriterException) {
            Log.d("ViewQrFragment", "generateQRCode: ${e.message}")
        }
        view.findViewById<ImageView>(R.id.imageView).setImageBitmap(bitmap)
    }

}