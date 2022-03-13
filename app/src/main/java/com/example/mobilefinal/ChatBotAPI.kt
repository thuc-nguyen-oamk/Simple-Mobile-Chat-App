package com.example.mobilefinal

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatBotAPI {
    @GET("api/botname/owner/message={msg}")
    fun getReply(@Path("msg") msg: String?): Call<JsonObject>
}