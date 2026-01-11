package pies3.workit.data.dto.post

import pies3.workit.data.dto.group.GroupListResponse

data class PostResponse(
    val id: String,
    val title: String,
    val activityType: String,
    val body: String?,
    val imageUrl: String?,
    val location: String?,
    val createdAt: String,
    val updatedAt: String?,
    val user: PostUserResponse,
    val group: GroupListResponse
)

data class PostUserResponse(
    val id: String,
    val name: String
)
