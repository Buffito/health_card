package com.theodoroskotoufos.healthcard.fragments

import android.app.KeyguardManager
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.theodoroskotoufos.healthcard.FingerPrintHelper
import com.theodoroskotoufos.healthcard.R
import java.lang.Exception
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class FingerFragment : Fragment() {
    lateinit var fingerPrintManager: FingerprintManager
    lateinit var keyguardManager: KeyguardManager

    lateinit var keyStore : KeyStore
    lateinit var keyGenerator: KeyGenerator
    var KEY_NAME = "key"

    lateinit var cipher: Cipher
    lateinit var cryptoObject : FingerprintManager.CryptoObject

    lateinit var cancellationSignal: CancellationSignal
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finger, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        keyguardManager = activity?.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        fingerPrintManager = activity?.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager

        if (!keyguardManager.isKeyguardSecure)
            Toast.makeText(requireContext(),"Lock screen security not enabled", Toast.LENGTH_SHORT).show()

        if(!fingerPrintManager.hasEnrolledFingerprints())
            Toast.makeText(requireContext(),"Who are you?", Toast.LENGTH_SHORT).show()

        validateFingerprint()
    }

    private fun validateFingerprint(){
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore")
            keyStore.load(null)
            keyGenerator.init(
                KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build())
            keyGenerator.generateKey()
        }catch (e: Exception){}

        if (initCipher()){
            cipher.let {
                cryptoObject = FingerprintManager.CryptoObject(it)
            }
        }

        val helper = FingerPrintHelper(requireContext())
        if (fingerPrintManager != null && cryptoObject != null)
            helper.startAuth(fingerPrintManager,cryptoObject) 
    }

    private fun initCipher(): Boolean {
        try {
            cipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        }catch (e: Exception){
            return false
        }

        try {
            keyStore.load(null)
            val key = keyStore.getKey(KEY_NAME,null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE,key)
            return true
        }catch (e: Exception){
            return false
        }
    }

    
}

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

    override fun onAuthenticationFailed() {
        super.onAuthenticationFailed()
        Toast.makeText(context,R.string.error,Toast.LENGTH_SHORT).show()
    }
}