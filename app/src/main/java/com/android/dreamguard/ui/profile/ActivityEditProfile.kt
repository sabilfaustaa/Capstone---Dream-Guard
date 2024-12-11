package com.android.dreamguard.ui.profile

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dreamguard.databinding.ActivityEditProfileBinding
import com.android.dreamguard.data.remote.models.UserProfile

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels {
        EditProfileViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupActions()

        viewModel.loadUserProfile()
    }

    private fun setupObservers() {
        viewModel.userProfile.observe(this) { profile ->
            binding.nameEditText.setText(profile.name)
            binding.emailEditText.setText(profile.email)
            binding.ageEditText.setText(profile.age?.toString())

            // Set gender button states
            when (profile.gender) {
                "male" -> {
                    binding.maleButton.isSelected = true
                    binding.femaleButton.isSelected = false
                }
                "female" -> {
                    binding.maleButton.isSelected = false
                    binding.femaleButton.isSelected = true
                }
                else -> {
                    binding.maleButton.isSelected = false
                    binding.femaleButton.isSelected = false
                }
            }

            // Set occupation dropdown
            binding.exposedDropdown.setText(profile.occupation ?: "", false)
        }

        viewModel.updateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            Log.d("ERROREDITPROFILE", error)
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupActions() {
        binding.saveProfileButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val age = binding.ageEditText.text.toString().toIntOrNull()
            val gender = when {
                binding.maleButton.isSelected -> "male"
                binding.femaleButton.isSelected -> "female"
                else -> null
            }
            val occupation = binding.exposedDropdown.text.toString()

            // Update user profile via ViewModel
            viewModel.updateUserProfile(name, email, age, gender, occupation, null)
        }

        binding.maleButton.setOnClickListener {
            binding.maleButton.isSelected = true
            binding.femaleButton.isSelected = false
        }

        binding.femaleButton.setOnClickListener {
            binding.maleButton.isSelected = false
            binding.femaleButton.isSelected = true
        }
    }
}
