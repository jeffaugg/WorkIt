package pies3.workit.data.api

import pies3.workit.data.dto.group.CreateGroupRequest
import pies3.workit.data.dto.group.CreateGroupResponse
import pies3.workit.data.dto.group.GroupListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GroupApi{
    @POST("groups")
    suspend fun create(@Body request: CreateGroupRequest): CreateGroupResponse

    @GET("groups")
    suspend fun getGroups(): List<GroupListResponse>
}