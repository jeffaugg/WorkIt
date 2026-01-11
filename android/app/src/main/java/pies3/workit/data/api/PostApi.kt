package pies3.workit.data.api

import pies3.workit.data.dto.post.CreatePostRequest
import pies3.workit.data.dto.post.PostResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PostApi {
    @POST("posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<PostResponse>

    @GET("posts")
    suspend fun getPosts(): Response<List<PostResponse>>
}
