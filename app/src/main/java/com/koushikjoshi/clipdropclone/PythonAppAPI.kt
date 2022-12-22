package com.koushikjoshi.clipdropclone

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PythonAppAPI {
    @POST("send-image")
    fun sendImage(@Body image: RequestBody): Call<ResponseBody>
}
