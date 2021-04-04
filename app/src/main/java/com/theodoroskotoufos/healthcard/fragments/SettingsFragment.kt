package com.theodoroskotoufos.healthcard.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.android.material.snackbar.Snackbar
import com.theodoroskotoufos.healthcard.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
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

        val editor = sharedPref.edit()

        val fillSwitch = findPreference<SwitchPreferenceCompat>("fill")
        val rememberSwitch = findPreference<SwitchPreferenceCompat>("remember")
        val bioSwitch = findPreference<SwitchPreferenceCompat>("bio")


        fillSwitch?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (fillSwitch != null && bioSwitch != null) {
                if (bioSwitch.isChecked){
                    bioSwitch.isChecked = false
                    fillSwitch.isChecked = true
                    showSnackbar(view)
                }
                editor.putBoolean("bio", bioSwitch.isChecked)
                editor.putBoolean("fill", fillSwitch.isChecked)
                editor.apply()
            }
            true
        }

        rememberSwitch?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (rememberSwitch != null) {
                editor.putBoolean("remember", rememberSwitch.isChecked)
                editor.apply()
            }
            true
        }

        bioSwitch?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (fillSwitch != null && bioSwitch != null) {
                if (fillSwitch.isChecked){
                    fillSwitch.isChecked = false
                    bioSwitch.isChecked = true
                    showSnackbar(view)
                }
                editor.putBoolean("fill", fillSwitch.isChecked)
                editor.putBoolean("bio", bioSwitch.isChecked)
                editor.apply()
            }
            true
        }


    }


    private fun showSnackbar(view: View){
        val mySnackbar =
            Snackbar.make(view, getString(R.string.auto_error), Snackbar.LENGTH_LONG)
        mySnackbar.view.setBackgroundColor(
            ContextCompat.getColor(
                requireActivity().application,
                R.color.green
            )
        )
        mySnackbar.show()
    }

}