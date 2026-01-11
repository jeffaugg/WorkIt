package pies3.workit.data.api

import pies3.workit.data.dto.user.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): UserResponse
}