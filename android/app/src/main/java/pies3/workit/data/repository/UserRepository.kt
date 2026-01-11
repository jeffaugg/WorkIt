package pies3.workit.data.repository

import pies3.workit.data.api.UserApi
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
}