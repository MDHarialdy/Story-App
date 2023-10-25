package com.mdh.storyapp.data.di

import android.content.Context
import com.mdh.storyapp.data.AppRepository
import com.mdh.storyapp.data.networking.ApiConfig
import com.mdh.storyapp.data.pref.UserPreference
import com.mdh.storyapp.data.pref.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): AppRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return AppRepository.getInstance(apiService, pref)
    }
}