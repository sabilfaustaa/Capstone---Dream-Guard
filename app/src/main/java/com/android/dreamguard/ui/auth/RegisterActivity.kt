package com.android.dreamguard.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dreamguard.R
import com.android.dreamguard.data.local.UserPreferences
import com.android.dreamguard.data.remote.api.ApiConfig
import com.android.dreamguard.data.remote.models.UserProfile
import com.android.dreamguard.data.repository.AuthRepository
import com.capstone.dreamguard.databinding.ActivityRegisterBinding
import com.android.dreamguard.ui.main.MainActivity
import com.android.dreamguard.ui.onboarding.OnboardingActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val authRepository by lazy { AuthRepository(ApiConfig.getApiService(this)) }
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        userPreferences = UserPreferences(this)

        setupGoogleSignIn()

        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val rePassword = binding.repasswordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
                showSnackbar("Please fill all fields")
                return@setOnClickListener
            }

            if (password != rePassword) {
                showSnackbar("Passwords do not match")
                return@setOnClickListener
            }

            showLoading(true)
            registerUserWithFirebase(name, email, password)
        }

        binding.registerButtonGoogle.setOnClickListener {
            showLoading(true)
            registerWithGoogle()
        }

        binding.toolbar.setNavigationOnClickListener {
            navigateToOnBoarding()
        }

        binding.loginText.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun registerUserWithFirebase(name: String, email: String, password: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val user = withContext(Dispatchers.IO) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                }
                val firebaseUser = user.user ?: throw Exception("Registration failed")

                val token = firebaseUser.getIdToken(true).await().token ?: throw Exception("Token retrieval failed")

                userPreferences.saveToken(token)

                sendTokenToBackend(name, email, firebaseUser.uid, token)
            } catch (e: Exception) {
                showLoading(false)
                showSnackbar("Error: ${e.message}")
            }
        }
    }

    private fun registerWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(Exception::class.java)
                if (account != null) {
                    handleGoogleRegistration(account)
                }
            } catch (e: Exception) {
                showLoading(false)
                showSnackbar("Google registration failed: ${e.message}")
            }
        }
    }

    private fun handleGoogleRegistration(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val signInMethods = withContext(Dispatchers.IO) {
                    firebaseAuth.fetchSignInMethodsForEmail(account.email!!).await()
                }

                if (signInMethods.signInMethods?.isNotEmpty() == true) {
                    showLoading(false)
                    showSnackbar("This account is already registered. Redirecting to login.")
                    navigateToLogin()
                    return@launch
                }

                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user ?: throw Exception("Registration failed")

                val token = firebaseUser.getIdToken(true).await().token ?: throw Exception("Token retrieval failed")

                sendTokenToBackend(account.displayName ?: "Unknown", account.email ?: "", firebaseUser.uid, token)
            } catch (e: Exception) {
                showLoading(false)
                showSnackbar("Error: ${e.message}")
            }
        }
    }


    private fun sendTokenToBackend(name: String, email: String, uid: String, token: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authRepository.registerNewUser(token, email, name)
                }

                if (response.isSuccessful) {
                    val userProfile = response.body() as? UserProfile ?: UserProfile(
                        uid = uid,
                        email = email,
                        name = name,
                        age = null,
                        gender = null,
                        occupation = null,
                        sleepGoal = null,
                        profilePicture = null,
                        createdAt = ""
                    )

                    userPreferences.saveUserProfile(userProfile)
                    userPreferences.saveToken(token)

                    showSnackbar("Registration successful!")
                    showLoading(false)
                    navigateToMain()
                } else {
                    showLoading(false)
                    showSnackbar("Registration failed: ${response.message()}")
                }
            } catch (e: Exception) {
                showLoading(false)
                showSnackbar("Error: ${e.message}")
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

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToOnBoarding() {
        val intent = Intent(this, OnboardingActivity::class.java)
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.registerButton.isEnabled = false
            binding.registerButton.text = ""
            binding.registerProgressBar.visibility = View.VISIBLE

            binding.registerButtonGoogle.isEnabled = false
            binding.registerButtonGoogle.text = ""
            binding.googleProgressBar.visibility = View.VISIBLE
        } else {
            binding.registerButton.isEnabled = true
            binding.registerButton.text = getString(R.string.button_register)
            binding.registerProgressBar.visibility = View.GONE

            binding.registerButtonGoogle.isEnabled = true
            binding.registerButtonGoogle.text = getString(R.string.button_google)
            binding.googleProgressBar.visibility = View.GONE
        }
    }


    private fun showSnackbar(message: String) {
        binding.root.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
    }

    companion object {
        private const val RC_SIGN_IN = 100
    }
}
