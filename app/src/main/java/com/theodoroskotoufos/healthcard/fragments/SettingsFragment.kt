package com.theodoroskotoufos.healthcard.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentTransaction
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.theodoroskotoufos.healthcard.MainActivity
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
        val langList = findPreference<ListPreference>("lang")
        val activity = MainActivity()
        val array = resources.getStringArray(R.array.lang_array)

        if (sharedPref.getString("lang","en") == "en"){
            activity.setLocale(requireActivity(), "en")
            langList?.setDefaultValue(array[0])
            langList?.summary = langList?.value
        }
        else{
            activity.setLocale(requireActivity(), "el")
            langList?.setDefaultValue(array[1])
            langList?.summary = langList?.value
        }


        fillSwitch?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (fillSwitch != null) {

                if (fillSwitch.isChecked)
                    editor.putBoolean("fill", true)
                else
                    editor.putBoolean("fill", false)

                editor.apply()

            }
            true
        }


        rememberSwitch?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            if (rememberSwitch != null) {
                if (rememberSwitch.isChecked)
                    editor.putBoolean("remember", true)
                else
                    editor.putBoolean("remember", false)

                editor.apply()

            }
            true
        }

        if (langList != null) {
            langList.summary = langList.value
            langList.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    langList.summary = newValue.toString()
                    if (newValue.toString() == array[0]){
                        activity.setLocale(requireActivity(), "en")
                        editor.putString("lang", "en")
                    }
                    else{
                        activity.setLocale(requireActivity(), "el")
                        editor.putString("lang", "el")
                    }
                    langList.summary = newValue.toString()
                    editor.apply()

                    true
                }
        }

    }

}