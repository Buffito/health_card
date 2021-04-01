package com.theodoroskotoufos.healthcard

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.zxing.integration.android.IntentIntegrator
import com.theodoroskotoufos.healthcard.fragments.*
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
        val fragment: Fragment
        if (intent.hasExtra("flag")) {
            fragment = when {
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
        }else if (intent.hasExtra("finger")){
            fragment = when {
                intent.getBooleanExtra("finger",false) -> {
                    MyProfileFragment()
                }
                else -> LoginFragment()
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
                val user = result.contents.trim()
                val bundle = Bundle()
                bundle.putString("user", user)

                val fragment = UserProfileFragment()
                fragment.arguments = bundle

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