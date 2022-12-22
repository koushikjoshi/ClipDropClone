package com.koushikjoshi.clipdropclone


import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ClipDropAPI {
    @Multipart
    @POST("remove-background/v1")
    fun removeBackground(
        @Header("x-api-key") apiKey: String,
        @Part imageFile: MultipartBody.Part
    ): Call<ResponseBody>
}
