package com.example.biometric

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.biometric.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var prompt: BiometricPrompt
    private lateinit var promptInfo : BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        setContentView(binding.root)

        checkBiometricSupported()
        authEvents()

        // authenticate user..
        binding.AuthenticateBtn.setOnClickListener{ prompt.authenticate(promptInfo) }

    }

    private fun authEvents() {
        prompt =
            BiometricPrompt(this@MainActivity, ContextCompat.getMainExecutor(this@MainActivity),
                object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        showMessage("احراز هویت با موفقیت انجام شـد", true)

                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        showMessage(errString.toString(), false)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        showMessage("دوباره امتحان کنید", false)

                    }
                })


        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric")
            .setDescription("Please place your finger on the fingerprint sensor..")
            .setNegativeButtonText("Cancel")
            .build()

    }


    @SuppressLint("SetTextI18n")
    private fun checkBiometricSupported() {
        BiometricManager.from(this).apply {
            when (canAuthenticate()) {
                // states
                BiometricManager.BIOMETRIC_SUCCESS -> {
                    showState("اهراز هویت کاربری")
                    binding.fingerprintImage.visibility = View.VISIBLE
                }
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> { showState( "متاسفم - سنسوری برای دستگاه شما یافت نشد!") }
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> { showState("دستگاه شما داده کلید اهراز هویتی را ذخیره ندارد\nلطفا تنظیمات دستگاه را بررسی نمائید!") }
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> { showState("سنسور مورد نظر دستگاه غیر قابل دسترس می باشد!") }
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> { showState("دستگاه شما از این قابلیت پشتیبانی نمیکند!") }
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> { showState("خطای امنیتی رخ داده است") }
                BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> { showState("وضعیت بیومتریک : نامشخص") }
                else -> {}
            }
        }
    }


    private fun showMessage(message : String , successAuth : Boolean) {
        binding.messageTv.text = message

        when(successAuth){
            true -> {
                binding.fingerprintImage.setImageResource(R.drawable.ic_baseline_fingerprint_24)
                binding.messageTv.setTextColor(ContextCompat.getColor(this@MainActivity,R.color.purple_200))
                binding.AuthenticateBtn.visibility = View.VISIBLE
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }

            false -> {
                binding.fingerprintImage.setImageResource(R.drawable.ic_baseline_fingerprint_gray_24)
                binding.messageTv.setTextColor(ContextCompat.getColor(this@MainActivity,R.color.black))
            }
        }
    }

    private fun showState(stateMessage : String) {
        binding.messageTv.text = stateMessage
    }
}