package pies3.workit.data.api

import pies3.workit.data.dto.group.CreateGroupRequest
import pies3.workit.data.dto.group.CreateGroupResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GroupApi{
    @POST("groups")
    suspend fun create(@Body request: CreateGroupRequest): CreateGroupResponse
}