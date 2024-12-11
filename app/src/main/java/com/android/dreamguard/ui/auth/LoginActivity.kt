package com.android.dreamguard.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.dreamguard.ui.main.MainActivity
import com.android.dreamguard.ui.onboarding.OnboardingActivity
import com.capstone.dreamguard.R
import com.capstone.dreamguard.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGoogleSignIn()
        setupListeners()
        observeViewModel()
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showSnackbar("Email and Password cannot be empty")
                return@setOnClickListener
            }

            showLoading(true)
            viewModel.login(email, password)
        }

        binding.googleButton.setOnClickListener {
            showLoading(true)
            signInWithGoogle()
        }

        binding.forgotPassword.setOnClickListener {
            navigateToForgotPassword()
        }

        binding.toolbar.setNavigationOnClickListener {
            navigateToOnBoarding()
        }

        binding.loginText.setOnClickListener() {
            navigateToRegister()
        }
    }

    private fun observeViewModel() {
        viewModel.authState.observe(this) { isSuccess ->
            showLoading(false)
            if (isSuccess) {
                showSnackbar("Login Successful")
                navigateToMain()
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            showLoading(false)
            if (!errorMessage.isNullOrEmpty()) {
                showSnackbar(errorMessage)
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleGoogleSignInResult(task)
            } else {
                showLoading(false)
                showSnackbar("Google Sign-In was canceled")
            }
        }

    private fun handleGoogleSignInResult(completedTask: com.google.android.gms.tasks.Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                Log.d("GoogleSignIn", "Account ID Token: ${account.idToken}")
                viewModel.googleSignIn(account.idToken ?: "")
            }
        } catch (e: ApiException) {
            showLoading(false)
            Log.e("GoogleSignIn", "Sign-In Failed: ${e.statusCode}")
            showSnackbar("Google Sign-In failed: ${e.statusCode}")
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToOnBoarding() {
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loginButton.text = ""
            binding.googleButton.isEnabled = false
            binding.loginButton.isEnabled = false
            binding.buttonLoading.visibility = View.VISIBLE
        } else {
            binding.loginButton.text = getString(R.string.button_login)
            binding.googleButton.isEnabled = true
            binding.loginButton.isEnabled = true
            binding.buttonLoading.visibility = View.GONE
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
