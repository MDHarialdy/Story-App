package com.mdh.storyapp.data

import androidx.lifecycle.asLiveData
import com.mdh.storyapp.data.networking.ApiService
import com.mdh.storyapp.data.pref.UserModel
import com.mdh.storyapp.data.pref.UserPreference
import okhttp3.MultipartBody
import okhttp3.RequestBody


class AppRepository private constructor(private val service: ApiService, private val userPreference: UserPreference) {

    //fungsi Daftar
    suspend fun register(name: String, email: String, password: String) =
        service.register(name, email, password)

    //fungsi login
    suspend fun login(email: String, password: String) =
        service.login(email, password)

    //mendapatkan stories
    suspend fun getStories() = service.getStories()

    //fungsi menyimpan preference key
    suspend fun saveSession(user: UserModel) =
        userPreference.saveSession(user)

    //mendapatkan sesi
    fun getSession() = userPreference.getSession().asLiveData()

    //fungsi logout
    suspend fun logout() {
        userPreference.logout()
    }

    //fungsi mendapatkan detail
    suspend fun getDetailStory(id: String) = service.getDetailStory(id)

    //fungsi upload file
    fun uploadFile(file: MultipartBody.Part, description: RequestBody) = service.uploadImage(file, description)


    companion object {
        @Volatile
        private var instance: AppRepository? = null
        fun getInstance(
            service: ApiService,
            userPreference: UserPreference
        ): AppRepository =
            instance ?: synchronized(this) {
                instance ?: AppRepository(service, userPreference)
            }.also { instance = it }
    }
}

