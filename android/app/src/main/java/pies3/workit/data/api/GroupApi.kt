package pies3.workit.data.api

import pies3.workit.data.dto.group.CreateGroupRequest
import pies3.workit.data.dto.group.CreateGroupResponse
import pies3.workit.data.dto.group.GroupListResponse
import pies3.workit.data.dto.group.JoinGroupResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GroupApi{
    @POST("groups")
    suspend fun create(@Body request: CreateGroupRequest): CreateGroupResponse

    @GET("groups")
    suspend fun getGroups(): List<GroupListResponse>

    @GET("groups/{id}")
    suspend fun getGroupById(@Path("id") id: String): GroupListResponse

    @POST("groups/{groupId}/users/{userId}")
    suspend fun joinGroup(
        @Path("groupId") groupId: String,
        @Path("userId") userId: String
    ): JoinGroupResponse
}