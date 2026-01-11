package pies3.workit.data.api

import pies3.workit.data.dto.user.UpdateUserRequest
import pies3.workit.data.dto.user.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): UserResponse

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body request: UpdateUserRequest
    ): UserResponse
}