package com.theodoroskotoufos.healthcard

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.preference.PreferenceManager
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.zxing.integration.android.IntentIntegrator
import com.theodoroskotoufos.healthcard.fragments.CreateProfileFragment
import com.theodoroskotoufos.healthcard.fragments.MainFragment
import com.theodoroskotoufos.healthcard.fragments.MyProfileFragment
import com.theodoroskotoufos.healthcard.fragments.UserProfileFragment
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var container: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        container = findViewById(R.id.fragment_container)


        changeLanguage()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }


    override fun onResume() {
        super.onResume()

        changeLanguage()

        if (intent.hasExtra("flag")) {
            val fragment: Fragment = when {
                intent.getStringExtra("flag").equals("profile") -> {
                    MyProfileFragment()
                }
                intent.getStringExtra("flag").equals("create") -> {
                    CreateProfileFragment()
                }
                else -> MainFragment()
            }

            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            try {
                supportFragmentManager.popBackStackImmediate(
                    fragment.toString(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
            } catch (e: IllegalStateException) {
            }

            transaction.addToBackStack(fragment.toString())
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commitAllowingStateLoss()
            supportFragmentManager.executePendingTransactions()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                val userlID = result.contents.trim()
                val mainKey = MasterKey.Builder(applicationContext)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
                    application,
                    "sharedPrefsFile",
                    mainKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
                val editor = sharedPref.edit()
                editor.putString("userID", userlID)
                editor.apply()

                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                try {
                    supportFragmentManager.popBackStackImmediate(
                        UserProfileFragment().toString(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                } catch (e: IllegalStateException) {
                }

                transaction.addToBackStack(UserProfileFragment().toString())
                transaction.replace(R.id.fragment_container, UserProfileFragment())
                transaction.commitAllowingStateLoss()
                supportFragmentManager.executePendingTransactions()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun setLocale(activity: Activity, languageCode: String?) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    private fun changeLanguage() {
        val mainKey = MasterKey.Builder(applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
            this,
            "sharedPrefsFile",
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val languageCode = sharedPref.getString("lang", "en")
        if (languageCode == "en")
            setLocale(this, "en")
        else
            setLocale(this, "el")
    }

}