package com.theodoroskotoufos.healthcard

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException

class ViewQrActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_qr)
       // supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val personalID: String = intent.getStringExtra("personalID").toString()
        val bitmap = generateQRCode(personalID)
        findViewById<ImageView>(R.id.imageView).setImageBitmap(bitmap)

    }

    private fun generateQRCode(text: String): Bitmap {
        val width = 800
        val height = 800
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) {
            Log.d("ViewQrActivity", "generateQRCode: ${e.message}")
        }
        return bitmap
    }
}