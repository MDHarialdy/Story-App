package com.mdh.storyapp.newstory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mdh.storyapp.data.AppRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewStoryViewModel(private val repository: AppRepository): ViewModel() {

    private val _uploadResponse = MutableLiveData<NewStoryResponse?>()
    val uploadResponse : LiveData<NewStoryResponse?> = _uploadResponse

    fun uploadFile(file: MultipartBody.Part, description: RequestBody) {
        val call = repository.uploadFile(file, description)
        call.enqueue(object : Callback<NewStoryResponse> {
            override fun onResponse(call: Call<NewStoryResponse>, response: Response<NewStoryResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _uploadResponse.postValue(responseBody)
                } else {
                    Log.e("NewStoryViewModel", "HTTP Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<NewStoryResponse>, t: Throwable) {
                Log.e("NewStoryViewModel", "Error: ${t.message}")
            }
        })
    }
}