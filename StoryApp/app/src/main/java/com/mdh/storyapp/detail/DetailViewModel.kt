package com.mdh.storyapp.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mdh.storyapp.data.AppRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailViewModel(private val repository: AppRepository): ViewModel() {

    private val _detailStory = MutableLiveData<DetailResponse>()
    private val _Story = MutableLiveData<Story>()
    val Story : LiveData<Story> = _Story

    fun getDetail(id: String){
        viewModelScope.launch {
            try {
                val response = repository.getDetailStory(id)
                _detailStory.postValue(response)
                _Story.postValue(response.story)
            } catch (e: HttpException){
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, DetailResponse::class.java)
                val errorMessage = errorBody.message
                _detailStory.postValue(errorBody)
                Log.e("DetailViewModel", "Fetch Data Error: $errorMessage")
            }
        }
    }
}