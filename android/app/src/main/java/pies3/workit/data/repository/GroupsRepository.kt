package pies3.workit.data.repository

import pies3.workit.data.api.GroupApi
import pies3.workit.data.dto.group.CreateGroupResponse
import pies3.workit.data.dto.group.CreateGroupRequest
import pies3.workit.data.dto.group.GroupListResponse
import javax.inject.Inject


class GroupsRepository @Inject constructor(
    private val groupApi: GroupApi
) {
    suspend fun createGroup(name: String, description: String?, imageUrl: String?): Result<CreateGroupResponse> {
        return try {
            val response = groupApi.create(CreateGroupRequest(name, description, imageUrl))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroups(): Result<List<GroupListResponse>> {
        return try {
            val response = groupApi.getGroups()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGroupById(groupId: String): Result<GroupListResponse> {
        return try {
            val response = groupApi.getGroupById(groupId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}