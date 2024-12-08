package com.android.dreamguard.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.android.dreamguard.data.local.UserPreferences
import com.capstone.dreamguard.databinding.ActivityRegisterBinding
import com.android.dreamguard.data.remote.api.ApiConfig
import com.android.dreamguard.data.repository.AuthRepository
import com.android.dreamguard.ui.main.MainActivity
import com.android.dreamguard.ui.onboarding.OnboardingActivity
import com.capstone.dreamguard.R
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val authRepository by lazy { AuthRepository(ApiConfig.getApiService()) }
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        setupGoogleSignIn()

        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val rePassword = binding.repasswordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != rePassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUserWithFirebase(name, email, password)
        }

        binding.toolbar.setNavigationOnClickListener  {
            navigateToOnBoarding()
        }

        binding.registerButtonGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(Exception::class.java)
            account?.let {
                authenticateWithFirebase(it)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Google sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun authenticateWithFirebase(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val authResult = firebaseAuth.signInWithCredential(credential).await()
                val firebaseUser = authResult.user ?: throw Exception("Authentication failed")
                val token = firebaseUser.getIdToken(true).await().token ?: throw Exception("Token retrieval failed")

//                saveToPreferences(firebaseUser.email ?: "", token)

                navigateToMain()
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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

//                saveToPreferences(email, token)

                sendTokenToBackend(email, password, token)
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendTokenToBackend(email: String, password: String, token: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    authRepository.registerNewUser(token, email, password)
                }

                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToOnBoarding() {
        val intent = Intent(this, OnboardingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun saveToPreferences(email: String, token: String) {
        userPreferences.saveUserEmail(email)
        userPreferences.saveToken(token)
    }
}
