package pies3.workit.data.dto.group

data class GroupListResponse(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val description: String?,
    val createdAt: String,
    val updatedAt: String?,
    val users: List<GroupUser>
)

data class GroupUser(
    val id: String,
    val name: String
)