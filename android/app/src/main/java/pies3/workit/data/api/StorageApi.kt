package pies3.workit.data.api

import okhttp3.MultipartBody
import pies3.workit.data.dto.storage.UploadResponse
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface StorageApi {
    @Multipart
    @POST("storage/upload")
    suspend fun upload(
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>
}