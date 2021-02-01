package com.theodoroskotoufos.healthcard.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.theodoroskotoufos.healthcard.CreateProfileActivity
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

        view.findViewById<Button>(R.id.loginButton).setOnClickListener{
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                R.id.action_mainFragment_to_loginFragment
            )
        }

        view.findViewById<Button>(R.id.scanButton).setOnClickListener{
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigate(
                R.id.action_mainFragment_to_barcodeCaptureFragment
            )
        }

        view.findViewById<Button>(R.id.createProfileButton).setOnClickListener{
            val intent = Intent (requireActivity(), CreateProfileActivity::class.java)
            startActivity(intent)
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

}