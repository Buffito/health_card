package com.theodoroskotoufos.healthcard

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.zxing.integration.android.IntentIntegrator
import com.theodoroskotoufos.healthcard.fragments.MainFragment
import com.theodoroskotoufos.healthcard.fragments.MyProfileFragment
import com.theodoroskotoufos.healthcard.fragments.UserProfileFragment

class MainActivity : AppCompatActivity() {
    private lateinit var container: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        container = findViewById(R.id.fragment_container)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onResume() {
        super.onResume()

        val fragment: Fragment
        if (intent.hasExtra("flag")) {
            fragment = if (intent.getBooleanExtra("flag", false))
                MyProfileFragment()
            else
                MainFragment()

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

}