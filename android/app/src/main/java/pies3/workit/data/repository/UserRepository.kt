package pies3.workit.data.repository

import pies3.workit.data.api.UserApi
import pies3.workit.data.dto.user.UpdateUserRequest
import pies3.workit.data.dto.user.UserResponse
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApi: UserApi
) {
    suspend fun getUserById(userId: String): Result<UserResponse> {
        return try {
            val response = userApi.getUserById(userId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(userId: String): Result<UserResponse> {
        return getUserById(userId)
    }

    suspend fun updateUser(
        userId: String,
        name: String? = null,
        email: String? = null,
        avatarUrl: String? = null
    ): Result<UserResponse> {
        return try {
            val request = UpdateUserRequest(name = name, email = email, avatarUrl = avatarUrl)
            val response = userApi.updateUser(userId, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}