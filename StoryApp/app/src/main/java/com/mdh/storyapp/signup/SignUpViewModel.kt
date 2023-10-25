package com.mdh.storyapp.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mdh.storyapp.data.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.regex.Pattern

class SignUpViewModel(private val repository: AppRepository) : ViewModel() {

    companion object {
        private const val TAG = "RegisterViewModel"
    }

    private val _isEmailValid = MutableLiveData<Boolean>()
    val isEmailValid: LiveData<Boolean> = _isEmailValid

    private val _isPassValid = MutableLiveData<Boolean>()
    val isPassValid: LiveData<Boolean> = _isPassValid

    private val _registrationResponse = MutableLiveData<SignUpResponse>()
    val registrationResponse: LiveData<SignUpResponse> = _registrationResponse

    fun performRegistration(name: String, email: String, password: String) {
        when {
            !isEmailValidInput(name, email, password) -> {
                _isEmailValid.postValue(false)
                Log.d(TAG, "Email Tidak Valid")
            }

            !isPasswordValid(name, email, password) -> {
                _isPassValid.postValue(false)
                Log.d(TAG, "Password Tidak Valid")
            }
        else -> {
            viewModelScope.launch {
                try {
                    _isEmailValid.postValue(true)
                    _isPassValid.postValue(true)
                    val response = withContext(Dispatchers.IO) {
                        repository.register(name, email, password)
                    }
                    _registrationResponse.postValue(response)
                    Log.d(TAG, "Registration Successful: ${response.message}")
                } catch (e: HttpException) {
                    val jsonInString = e.response()?.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, SignUpResponse::class.java)
                    val errorMessage = errorBody.message
                    _registrationResponse.postValue(errorBody)
                    Log.e(TAG, "Registration Error: $errorMessage")
                  }
                }
            }
        }
    }

    private fun isEmailValidInput(name: String, email: String, password: String): Boolean {
        val emailPattern = Pattern.compile("""[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""")
        return name.isNotBlank() && emailPattern.matcher(email).matches() && password.isNotBlank()
    }

    private fun isPasswordValid(name: String, email: String, password: String): Boolean {
        val passwordPattern = Pattern.compile("^.{8,}\$")
        return name.isNotBlank() && email.isNotBlank() && passwordPattern.matcher(password).matches()
    }
}