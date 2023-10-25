package com.mdh.storyapp.data.networking

import com.mdh.storyapp.detail.DetailResponse
import com.mdh.storyapp.login.LoginResponse
import com.mdh.storyapp.main.MainResponse
import com.mdh.storyapp.newstory.NewStoryResponse
import com.mdh.storyapp.signup.SignUpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): SignUpResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(): MainResponse

    @GET("stories/{id}")
    suspend fun getDetailStory(
        @Path("id") id : String
    ): DetailResponse

    @Multipart
    @POST("stories")
    fun uploadImage(
    @Part file: MultipartBody.Part,
    @Part("description") description: RequestBody,
    ): Call <NewStoryResponse>

}