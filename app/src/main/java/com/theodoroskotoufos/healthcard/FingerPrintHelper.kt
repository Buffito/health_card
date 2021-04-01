package com.theodoroskotoufos.healthcard

import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.os.TokenWatcher
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.theodoroskotoufos.healthcard.fragments.UserProfileFragment

class FingerPrintHelper(private val context: Context) : FingerprintManager.AuthenticationCallback() {
    lateinit var cancellationSignal: CancellationSignal

    fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject){
        cancellationSignal = CancellationSignal()

        manager.authenticate(cryptoObject,cancellationSignal,0,this,null)

    }

    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
        super.onAuthenticationError(errorCode, errString)
    }

    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
        super.onAuthenticationHelp(helpCode, helpString)
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        super.onAuthenticationSucceeded(result)

        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("finger", true)
        context.startActivity(intent)
    }

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        Toast.makeText(context,R.string.error,Toast.LENGTH_SHORT).show()
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra("finger", false)
        context.startActivity(intent)
    }
}
