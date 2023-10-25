package com.mdh.storyapp.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
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
import com.mdh.storyapp.main.MainActivity
import com.mdh.storyapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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
        binding.loginButton.setOnClickListener {
            val emailEditText = binding.emailEditText.text.toString()
            val passwordEditText = binding.passwordEditText.text.toString()
            Log.d(ContentValues.TAG, "Button Register clicked")

            if (emailEditText.isEmpty()) {
                binding.emailEditTextLayout.error = "Email tidak boleh kosong"
            } else if (passwordEditText.isEmpty()) {
                binding.passwordEditTextLayout.error = "Password Tidak Boleh kosong"
            } else {
                if (emailEditText.isNotEmpty() && passwordEditText.isNotEmpty()) {
                    binding.emailEditTextLayout.error = null
                    binding.passwordEditTextLayout.error = null
                    loginViewModel.performLogin(
                        emailEditText,
                        passwordEditText
                    )
                }
            }
        }
    }

    private fun setupViewModel(){
        loginViewModel.isEmailValid.observe(this){
            if (!it){
                Toast.makeText(this, "Email Tidak Valid", Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.isPassValid.observe(this){
            if (!it){
                Toast.makeText(this, "Password Minimal 8 Huruf", Toast.LENGTH_SHORT).show()
            }
        }
        loginViewModel.loginResponse.observe(this) {
            if (it.error) {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                Toast.makeText(this, "Login success", Toast.LENGTH_LONG).show()
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
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }
}