package com.android.dreamguard.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.dreamguard.ui.home.HomeActivity
import com.android.dreamguard.ui.main.MainActivity
import com.android.dreamguard.ui.onboarding.OnboardingActivity
import com.capstone.dreamguard.R
import com.android.dreamguard.data.local.UserPreferences
import com.capstone.dreamguard.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var userPreferences: UserPreferences
    private var isFirstLaunch: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        userPreferences = UserPreferences(this)
        isFirstLaunch = checkFirstLaunch()

        setupGoogleSignIn()
        setupListeners()
    }

    private fun checkFirstLaunch(): Boolean {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        return sharedPreferences.getBoolean("is_first_launch", true).also {
            if (it) {
                sharedPreferences.edit().putBoolean("is_first_launch", false).apply()
            }
        }
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

            showLoading(true, isGoogleLogin = false)
            loginWithEmailPassword(email, password)
        }

        binding.googleButton.setOnClickListener {
            showLoading(true, isGoogleLogin = true)
            signInWithGoogle()
        }

        binding.forgotPassword.setOnClickListener {
            navigateToForgotPassword()
        }

        binding.loginText.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun loginWithEmailPassword(email: String, password: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val user = withContext(Dispatchers.IO) {
                    firebaseAuth.signInWithEmailAndPassword(email, password).await()
                }
                val token = user.user?.getIdToken(true)?.await()?.token
                    ?: throw Exception("Failed to retrieve token")

                userPreferences.saveToken(token)
                showSnackbar("Login Successful")
                showLoading(false, isGoogleLogin = false)
                navigateToNextScreen()
            } catch (e: Exception) {
                showSnackbar("Login failed: ${e.message}")
                showLoading(false, isGoogleLogin = false)
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
                showSnackbar("Google Sign-In was canceled")
                showLoading(false, isGoogleLogin = true)
            }
        }

    private fun handleGoogleSignInResult(task: com.google.android.gms.tasks.Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val authResult = firebaseAuth.signInWithCredential(credential).await()
                        val token = authResult.user?.getIdToken(true)?.await()?.token
                            ?: throw Exception("Failed to retrieve token")

                        userPreferences.saveToken(token)
                        showSnackbar("Google Login Successful")
                        showLoading(false, isGoogleLogin = true)
                        navigateToNextScreen()
                    } catch (e: Exception) {
                        showSnackbar("Google Login failed: ${e.message}")
                        showLoading(false, isGoogleLogin = true)
                    }
                }
            }
        } catch (e: ApiException) {
            showSnackbar("Google Sign-In failed: ${e.statusCode}")
            showLoading(false, isGoogleLogin = true)
        }
    }

    private fun navigateToNextScreen() {
        if (isFirstLaunch) {
            navigateToMain()
        } else {
            navigateToHome()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean, isGoogleLogin: Boolean) {
        if (isLoading) {
            if (isGoogleLogin) {
                binding.googleButton.text = ""
                binding.googleButton.isEnabled = false
                binding.googleLoading.visibility = View.VISIBLE
            } else {
                binding.loginButton.text = ""
                binding.loginButton.isEnabled = false
                binding.buttonLoading.visibility = View.VISIBLE
            }
        } else {
            if (isGoogleLogin) {
                binding.googleButton.text = getString(R.string.button_google)
                binding.googleButton.isEnabled = true
                binding.googleLoading.visibility = View.GONE
            } else {
                binding.loginButton.text = getString(R.string.button_login)
                binding.loginButton.isEnabled = true
                binding.buttonLoading.visibility = View.GONE
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }
}
