package com.android.dreamguard.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dreamguard.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.forgotPasswordButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                sendPasswordResetEmail(email)
            }
        }
    }

    private fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset link sent to $email", Toast.LENGTH_SHORT).show()
                    navigateToVerification(email)
                } else {
                    Toast.makeText(
                        this,
                        task.exception?.message ?: "Failed to send reset email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun navigateToVerification(email: String) {
        val intent = Intent(this, VerificationActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }
}
