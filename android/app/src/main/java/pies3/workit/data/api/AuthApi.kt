package pies3.workit.data.api

import pies3.workit.data.dto.auth.LoginRequest
import pies3.workit.data.dto.auth.LoginResponse
import pies3.workit.data.dto.auth.SignupRequest
import pies3.workit.data.dto.auth.SignupResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: SignupRequest): SignupResponse
}
