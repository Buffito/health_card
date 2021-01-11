package com.theodoroskotoufos.healthcard

import android.graphics.Color
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.WindowManager
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.zxing.WriterException

class ViewQrActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_qr)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val personalID: String = intent.getStringExtra("personalID").toString()

        val manager: WindowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val display: Display = manager.getDefaultDisplay()
        val point = Point()
        display.getSize(point)
        val width: Int = point.x
        val height: Int = point.y
        var smallerDimension = if (width < height) width else height
        smallerDimension = smallerDimension * 3 / 4


        val qrgEncoder = QRGEncoder(
            personalID,
            null,
            QRGContents.Type.TEXT,
            smallerDimension
        )
        qrgEncoder.colorBlack = Color.BLACK
        qrgEncoder.colorWhite = Color.TRANSPARENT

        try {
            // Getting QR-Code as Bitmap
            val bitmap = qrgEncoder.bitmap
            // Setting Bitmap to ImageView
            findViewById<ImageView>(R.id.imageView).setImageBitmap(bitmap)
        } catch (e: WriterException) {
            Log.e("TAG", e.toString())
        }
    }
}