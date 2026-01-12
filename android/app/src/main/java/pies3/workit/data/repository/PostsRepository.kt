package pies3.workit.data.repository

import pies3.workit.data.api.PostApi
import pies3.workit.data.dto.post.CreatePostRequest
import pies3.workit.data.dto.post.PostResponse
import pies3.workit.data.dto.post.UpdatePostRequest
import javax.inject.Inject

class PostsRepository @Inject constructor(
    private val postApi: PostApi
) {
    suspend fun createPost(
        title: String,
        activityType: String,
        body: String?,
        imageUrl: String?,
        location: String?,
        groupId: String,
        userId: String
    ): Result<PostResponse> {
        return try {
            val request = CreatePostRequest(
                title = title,
                activityType = activityType,
                body = body,
                imageUrl = imageUrl,
                location = location,
                groupId = groupId,
                userId = userId
            )
            
            val response = postApi.createPost(request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erro ao criar publicação"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPosts(): Result<List<PostResponse>> {
        return try {
            val response = postApi.getPosts()
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erro ao carregar publicações"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPostsByGroup(groupId: String): Result<List<PostResponse>> {
        return try {
            val response = postApi.getPostsByGroup(groupId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erro ao carregar publicações do grupo"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPostById(postId: String): Result<PostResponse> {
        return try {
            val response = postApi.getPostById(postId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erro ao carregar publicação"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePost(postId: String): Result<Unit> {
        return try {
            val response = postApi.deletePost(postId)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Erro ao excluir publicação"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePost(
        postId: String,
        title: String,
        activityType: String,
        body: String?,
        imageUrl: String?,
        location: String?,
        groupId: String
    ): Result<PostResponse> {
        return try {
            val request = UpdatePostRequest(
                title = title,
                activityType = activityType,
                body = body,
                imageUrl = imageUrl,
                location = location,
                groupId = groupId
            )
            
            val response = postApi.updatePost(postId, request)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erro ao atualizar publicação"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
