package com.android.dreamguard.ui.auth

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dreamguard.R
import com.capstone.dreamguard.databinding.ActivityVerificationBinding
import com.google.firebase.auth.FirebaseAuth

class VerificationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerificationBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var isCountdownActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val email = intent.getStringExtra("email")
        if (email != null) {
            binding.emailUser.text = email
        }

        startCountdown()
        setupResendEmailListener(email)

        binding.verifyButton.setOnClickListener {
            navigateToLogin()
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun startCountdown() {
        val totalTime = 60000L // 60 detik
        val interval = 1000L

        object : CountDownTimer(totalTime, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                binding.textVerify.text = getString(R.string.text_verify_information, "0:$secondsLeft")
                binding.textVerify.isClickable = false
                binding.textVerify.isFocusable = false
            }

            override fun onFinish() {
                binding.textVerify.text = "Resend email here"
                binding.textVerify.isClickable = true
                binding.textVerify.isFocusable = true
            }
        }.start()
    }

    private fun setupResendEmailListener(email: String?) {
        binding.textVerify.setOnClickListener {
            if (binding.textVerify.isClickable) {
                resendVerificationEmail(email)
            } else {
                Toast.makeText(this, "Please wait for the countdown to finish.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resendVerificationEmail(email: String?) {
        if (email != null) {
            firebaseAuth.currentUser?.sendEmailVerification()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Verification email resent to $email", Toast.LENGTH_SHORT).show()
                        startCountdown() // Restart countdown setelah resend
                    } else {
                        Toast.makeText(
                            this,
                            task.exception?.message ?: "Failed to resend email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            Toast.makeText(this, "No email found to resend verification", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
