package com.mdh.storyapp.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues.TAG
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.mdh.storyapp.ViewModelFactory
import com.mdh.storyapp.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val registerViewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        setupViewModel()
        playAnimation()

    }


    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {

            val nameEditText = binding.nameEditText.text.toString()
            val emailEditText = binding.emailEditText.text.toString()
            val passwordEditText = binding.passwordEditText.text.toString()
            Log.d(TAG, "Button Register clicked")

            if (nameEditText.isEmpty()) {
                binding.nameEditTextLayout.error = "Nama tidak boleh kosong"
            } else if (emailEditText.isEmpty()) {
                binding.emailEditTextLayout.error = "Email tidak boleh kosong"
            } else if (passwordEditText.isEmpty()) {
                binding.passwordEditTextLayout.error = "Password Tidak Boleh kosong"
            } else {
                if (nameEditText.isNotEmpty() && emailEditText.isNotEmpty() && passwordEditText.isNotEmpty()) {
                    binding.nameEditTextLayout.error = null
                    binding.emailEditTextLayout.error = null
                    binding.passwordEditTextLayout.error = null
                    registerViewModel.performRegistration(
                        nameEditText,
                        emailEditText,
                        passwordEditText
                    )
                }
            }
        }
    }

    private fun setupViewModel() {
        registerViewModel.isEmailValid.observe(this){
            if (!it){
                Toast.makeText(this, "Email Tidak Valid", Toast.LENGTH_SHORT).show()
            }
        }

        registerViewModel.isPassValid.observe(this){
            if (!it){
                Toast.makeText(this, "Password Minimal 8 Huruf", Toast.LENGTH_SHORT).show()
            }
        }

        registerViewModel.registrationResponse.observe(this) {
            if (it.error) {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@SignUpActivity, "Register success", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(100)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(100)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 100
        }.start()
    }
}