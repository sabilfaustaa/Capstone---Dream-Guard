package com.android.dreamguard.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dreamguard.R
import com.android.dreamguard.data.remote.models.UserProfile
import com.android.dreamguard.ui.home.HomeActivity
import com.bumptech.glide.Glide
import com.capstone.dreamguard.databinding.ActivityEditProfileBinding
import java.io.File

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels {
        EditProfileViewModelFactory(this)
    }

    private var selectedImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupOccupationDropdown()
        setupObservers()
        setupActions()

        viewModel.loadUserProfile()
    }

    private fun setupOccupationDropdown() {
        val occupations = listOf("Engineer", "Doctor", "Teacher", "Student", "Others")
        val adapter = ArrayAdapter(this, R.layout.dropdown_occupation, occupations)
        binding.exposedDropdown.setAdapter(adapter)
    }

    private fun setupObservers() {
        viewModel.userProfile.observe(this) { profile ->
            binding.nameEditText.setText(profile?.name)
            binding.emailEditText.setText(profile?.email)
            binding.ageEditText.setText(profile?.age?.toString())

            when (profile?.gender) {
                "male" -> setGenderSelection(true)
                "female" -> setGenderSelection(false)
                else -> setGenderSelection(null)
            }

            binding.exposedDropdown.setText(profile?.occupation ?: "", false)

            if (profile?.profilePicture.isNullOrEmpty()) {
                binding.profileUser.setImageResource(R.drawable.shinon)
            } else {
                Glide.with(this)
                    .load(profile?.profilePicture)
                    .placeholder(R.drawable.shinon)
                    .into(binding.profileUser)
            }
        }

        viewModel.updateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupActions() {
        binding.profileChangeButton.setOnClickListener {
            openGallery()
        }

        binding.toolbar.setNavigationOnClickListener {
            navigateToHome()
        }

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

            viewModel.updateUserProfile(name, email, age, gender, occupation, selectedImageFile)
        }

        binding.maleButton.setOnClickListener {
            setGenderSelection(true)
        }

        binding.femaleButton.setOnClickListener {
            setGenderSelection(false)
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private fun copyUriToFile(uri: Uri): File {
        val tempFile = File.createTempFile("temp_image", ".jpg", cacheDir)
        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }


    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let {
                    binding.profileUser.setImageURI(it)
                    selectedImageFile = copyUriToFile(it)
                }
            }
        }

    private fun setGenderSelection(isMale: Boolean?) {
        when (isMale) {
            true -> {
                binding.maleButton.isSelected = true
                binding.maleButton.setBackgroundColor(getColor(R.color.purple))
                binding.maleButton.setTextColor(getColor(R.color.white))
                binding.femaleButton.isSelected = false
                binding.femaleButton.setBackgroundColor(getColor(R.color.white))
                binding.femaleButton.setTextColor(getColor(R.color.black))
            }
            false -> {
                binding.femaleButton.isSelected = true
                binding.femaleButton.setBackgroundColor(getColor(R.color.purple))
                binding.femaleButton.setTextColor(getColor(R.color.white))
                binding.maleButton.isSelected = false
                binding.maleButton.setBackgroundColor(getColor(R.color.white))
                binding.maleButton.setTextColor(getColor(R.color.black))
            }
            else -> {
                binding.maleButton.isSelected = false
                binding.femaleButton.isSelected = false
                binding.maleButton.setBackgroundColor(getColor(R.color.white))
                binding.femaleButton.setBackgroundColor(getColor(R.color.white))
                binding.maleButton.setTextColor(getColor(R.color.black))
                binding.femaleButton.setTextColor(getColor(R.color.black))
            }
        }
    }


    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
