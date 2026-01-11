package pies3.workit.data.api

import pies3.workit.data.dto.post.CreatePostRequest
import pies3.workit.data.dto.post.PostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PostApi {
    @POST("posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<PostResponse>

    @GET("posts")
    suspend fun getPosts(): Response<List<PostResponse>>

    @GET("posts/group/{groupId}")
    suspend fun getPostsByGroup(@Path("groupId") groupId: String): Response<List<PostResponse>>

    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") postId: String): Response<PostResponse>

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") postId: String): Response<Unit>
}
