package com.theodoroskotoufos.healthcard.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.zxing.integration.android.IntentIntegrator
import com.theodoroskotoufos.healthcard.CaptureActivity
import com.theodoroskotoufos.healthcard.MyProfileActivity
import com.theodoroskotoufos.healthcard.R
import com.theodoroskotoufos.healthcard.UserProfileActivity


class BarcodeCaptureFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        scanQRCode()
        return inflater.inflate(R.layout.fragment_barcode_capture, container, false)
    }


    private fun scanQRCode() {
        val integrator = IntentIntegrator(requireActivity()).apply {
            captureActivity = CaptureActivity::class.java
            setOrientationLocked(false)
            setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            setPrompt("Scanning Code")
        }
        integrator.initiateScan()
    }

    // Get the results:
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null){
                Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                    R.id.action_barcodeCaptureFragment_to_mainFragment
                )
                Toast.makeText(requireActivity(), "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(requireActivity(), UserProfileActivity::class.java)
                intent.putExtra("personalID", result.contents)
                startActivity(intent)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


}