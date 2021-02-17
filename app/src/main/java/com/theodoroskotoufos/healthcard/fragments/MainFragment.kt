package com.theodoroskotoufos.healthcard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.zxing.integration.android.IntentIntegrator
import com.theodoroskotoufos.healthcard.CaptureActivity
import com.theodoroskotoufos.healthcard.R


class MainFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.findViewById<Button>(R.id.loginButton).setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                R.id.action_mainFragment_to_loginFragment
            )
        }

        view.findViewById<Button>(R.id.scanButton).setOnClickListener {
            scanQRCode()
        }

        view.findViewById<Button>(R.id.createProfileButton).setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                R.id.action_mainFragment_to_create_profile_fragment
            )
        }
    }

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                R.id.action_mainFragment_to_permissionsFragment
            )
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