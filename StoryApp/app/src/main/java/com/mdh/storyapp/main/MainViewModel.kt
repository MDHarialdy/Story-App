package com.mdh.storyapp.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mdh.storyapp.data.AppRepository
import com.mdh.storyapp.data.pref.UserModel
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
    }

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory : LiveData<List<ListStoryItem>> = _listStory

    private val _stories = MutableLiveData<MainResponse>()

    fun getSession(): LiveData<UserModel> {
        return repository.getSession()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun fetchStories() {
        viewModelScope.launch {
            try {
                val response = repository.getStories()
                _stories.value = response
                _listStory.value = response.listStory
                Log.d(TAG, response.message)
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, MainResponse::class.java)
                val errorMessage = errorBody.message
                _stories.postValue(errorBody)
                Log.e(TAG, "Fetch Data Error: $errorMessage")
            }
        }
    }
}