package pies3.workit.data.repository

import pies3.workit.data.api.AuthApi
import pies3.workit.data.dto.LoginRequest
import pies3.workit.data.dto.LoginResponse
import pies3.workit.data.dto.SignupRequest
import pies3.workit.data.dto.SignupResponse
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi
) {
    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<SignupResponse> {
        return try {
            val response = authApi.register(SignupRequest(name, email, password))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
