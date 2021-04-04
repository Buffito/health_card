package com.theodoroskotoufos.healthcard

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal

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

    }

}
