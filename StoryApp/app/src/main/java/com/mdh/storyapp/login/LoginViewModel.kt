package com.mdh.storyapp.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mdh.storyapp.data.AppRepository
import com.mdh.storyapp.data.pref.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.regex.Pattern

class LoginViewModel(private val repository: AppRepository) : ViewModel() {
    companion object {
        private const val TAG = "LoginViewModel"
    }

    private val _isEmailValid = MutableLiveData<Boolean>()
    val isEmailValid: LiveData<Boolean> = _isEmailValid

    private val _isPassValid = MutableLiveData<Boolean>()
    val isPassValid: LiveData<Boolean> = _isPassValid

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    fun performLogin(email: String, password: String) {
        when {
            !isEmailValidInput(email, password) -> {
                _isEmailValid.postValue(false)
                Log.d(TAG, "Email Tidak Valid")
            }

            !isPasswordValid(email, password) -> {
                _isPassValid.postValue(false)
                Log.d(TAG, "Password Tidak Valid")
            }
            else -> {
                viewModelScope.launch {
                    try {
                        _isEmailValid.postValue(true)
                        _isPassValid.postValue(true)
                        val response = withContext(Dispatchers.IO) {
                            repository.login(email, password)
                        }
                        _loginResponse.postValue(response)
                        saveSession(
                            UserModel(
                                token = response.loginResult.token,
                                userId = response.loginResult.userId,
                                name = response.loginResult.name,
                                email = email,
                                isLogin = true
                            )
                        )
                        Log.d(TAG, "Login Successful: ${response.message}")
                    } catch (e: HttpException) {
                        val jsonInString = e.response()?.errorBody()?.string()
                        val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
                        val errorMessage = errorBody.message
                        _loginResponse.postValue(errorBody)
                        Log.e(TAG, "Login Error: $errorMessage")
                    }
                }
            }
        }
    }

    private fun isEmailValidInput(email: String, password: String): Boolean {
        val emailPattern = Pattern.compile("""[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""")
        return emailPattern.matcher(email).matches() && password.isNotBlank()
    }

    private fun isPasswordValid(email: String, password: String): Boolean {
        val passwordPattern = Pattern.compile("^.{8,}\$")
        return email.isNotBlank() && passwordPattern.matcher(password).matches()
    }

    private fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}
